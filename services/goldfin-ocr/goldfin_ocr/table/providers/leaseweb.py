# Copyright (c) 2018 Goldfin Systems LLC.  All rights reserved.

"""Leaseweb invoice analyzer"""

import logging
import re

from goldfin_ocr.api.models.invoice import Invoice
from goldfin_ocr.api.models.invoice_item import InvoiceItem
import goldfin_ocr.table.tabularquery as tq
import goldfin_ocr.table.tabularmodel as tm
import goldfin_ocr.data_utils as data_utils
import goldfin_ocr.util as util
import goldfin_ocr.vendors as vendors

# Define logger
logger = logging.getLogger(__name__)


class LeasewebProcessor:
    """Leaseweb Invoice Processor"""

    def __init__(self, tabular_model):
        self._tabular_model = tabular_model
        self._engine = tq.QueryEngine(self._tabular_model)

    @staticmethod
    def conforms(model):
        """Returns True if we can find www.leaseweb.com identification at least
        once in the body of the first page"""

        engine = tq.QueryEngine(model)
        count = 0
        lines = engine.tabular_query().matches_type(tm.Line).page(
            1).matches_regex(
            r'www\.leaseweb\.com').generate()
        for line in lines:
            logger.debug(line)
            count += 1
        logger.debug("count of www.leaseweb.com: {0}".format(count))
        return count >= 1

    def name(self):
        return vendors.LEASEWEB

    def get_content(self):
        """Analyzes the invoice tabular model and returns semantic content"""

        # Extract invoice header information available from text blocks.
        logger.info(util.dump_to_json(self._tabular_model))
        invoice = Invoice()
        invoice.vendor_identifier = vendors.LEASEWEB
        invoice.items = []

        # Find first page and define geometric positions of header info.
        page1 = self._engine.tabular_page_query(1).cut().first()
        identifier_region = tm.Region(left=856, top=360, right=1154,
                                      bottom=389)
        eff_date_region = tm.Region(left=856, top=410, right=1096, bottom=445)

        # Find identifier code.  Strip out invoice number tag in case OCR
        # glommed it in with identifier.
        identifier_line = self._engine.tabular_line_query(
            root=page1).intersects(
            identifier_region).first()
        identifier = identifier_line.text.strip()
        invoice.identifier = re.sub(r'^\s*Invoice number:\s*', '', identifier)

        # Find effective date.
        eff_date_line = self._engine.tabular_line_query(root=page1).intersects(
            eff_date_region).first()
        invoice.effective_date = data_utils.extract_date(eff_date_line.text.strip())
        if invoice.effective_date is None:
            logger.warning("Unable to extract date: {0}".format(eff_date_line))

        # Currency has to be inferred from what we see on the first page:
        # Price total header shows Total(€), Total(US$), or Total(SG$).
        total_regex = r'^.*Total\s*\((.*)\).*$'
        total_header = self._engine.tabular_line_query(
            root=page1).matches_regex(
            total_regex).first()
        if total_header:
            currency_match = re.match(total_regex, total_header.text.strip())
            raw_currency = currency_match.group(1)
            if raw_currency == '€':
                invoice.currency = "EUR"
            elif raw_currency == 'US$':
                invoice.currency = "USD"
            elif raw_currency == 'SG$':
                invoice.currency = "SGD"
            else:
                logger.warning(
                    "Unable to determine currency for invoice: identifier={0}, text={1}".format(
                        invoice.identifier, total_header.text))

        # Subtotal, tax, and total are on the last page but we can't be too
        # sure of exact location.  Instead, use a brute force search for
        # the tag strings, then look right to get currency numbers.
        total_amount_regex = r'^\s*Total Amount Due'
        subtotal_amount_regex = r'^\s*(Subtotal customer|Subtotal this invoice)'
        invoice.total_amount = self._get_summary_total(total_amount_regex)
        invoice.subtotal_amount = self._get_summary_total(
            subtotal_amount_regex)

        # Tax only shown on US invoices, it seems.
        invoice.tax = self._get_summary_total(r'^\s*Tax\s*\(US\$\)')

        # That was just warming up.  Now we need to handle items.  To do this
        # we'll construct a frame that gives the bounds of the page that
        # contain invoice items.

        # Let's first establish a top bound on page 1 which is defined by the
        # bottom of the line that contains the words 'Service'
        # and 'Description'.  We add extra pixels to ensure we're below it.
        first_page = 1
        first_page_top = 0
        extra_pixels = 5
        for line in self._engine.tabular_line_query().page(1).matches_regex(
                '^.*(Service|Description)').generate():
            first_page_top = max(first_page_top, line.region.bottom + extra_pixels)

        # Similarly get the bottom which much be above the subtotal line.
        last_page = 0
        last_page_bottom = 0
        for line in self._engine.tabular_line_query().matches_regex(
                subtotal_amount_regex).generate():
            last_page = line.page_number
            last_page_bottom = max(last_page_bottom, line.region.top)

        # The left edge is the minimal left value of the text line that starts
        # with the word 'Serverhosting'.
        left_margin_regex = r'^Serverhosting'
        left_margin = 99999
        for line in self._engine.tabular_line_query().matches_regex(
                left_margin_regex).generate():
            left_margin = min(left_margin, line.region.left)

        # The right edge is the maximum right value of a text line that
        # contains the value 0.00/0,00.
        right_margin_regex = r'(0\.00|0,00)'
        right_margin = 0
        for line in self._engine.tabular_line_query().matches_regex(
                right_margin_regex).generate():
            right_margin = max(right_margin, line.region.right)

        # The top edge is the bottom of any row containing the
        # phrase 'Customer number'.  This appears in the first page
        # so we have to work up.
        top_margin_regex = '^Customer number:'
        top_margin = 99999
        for line in self._engine.tabular_line_query().page(1).matches_regex(
                top_margin_regex).generate():
            top_margin = min(top_margin, line.region.bottom)

        # The bottom edge is the top value of any row with the disclaimer text at the 
        # bottom of the page + a fudge factor since some invoices seem to overlap.
        bottom_margin_regex = r'^All services and equipment are'
        bottom_margin = 99999
        for line in self._engine.tabular_line_query().page(1).matches_regex(
                bottom_margin_regex).generate():
            bottom_margin = min(bottom_margin, line.region.top)
        bottom_margin += 10

        data_region = tm.Region(left=left_margin, top=top_margin,
                                right=right_margin, bottom=bottom_margin)

        # Log results of our discoveries.
        logger.info("First page: page_number={0}, top={1}".format(first_page,
                                                                  first_page_top))
        logger.info("Bounding data region: {0}".format(data_region))
        logger.info("Last page: page_number={0}, bottom={1}".format(last_page,
                                                                    last_page_bottom))

        # We can now construct a predicate function that selects only
        # entities within this window.
        def data_window(entity):
            # Items above the invoice data on first page.
            if entity.page_number == 1 and entity.region.bottom < first_page_top:
                logger.debug("BEFORE: {0}".format(entity))
                return False

            # Items below the invoice data on the last page.
            if entity.page_number > last_page:
                logger.debug("AFTER #1: {0}".format(entity))
                return False
            elif entity.page_number == last_page and entity.region.bottom > last_page_bottom:
                logger.debug("AFTER #2: {0}".format(entity))
                return False

            # Items that are outside the frame containing invoice data.
            if not data_region.contains(entity.region):
                logger.debug("OUTSIDE: {0}".format(entity))
                return False

            # If we get here this is something we need to look at.
            logger.debug("SELECTED: {0}".format(entity))
            return True

        # Iterate over the invoice line items.
        invoice_item_left_edge = 340
        logger.info("Scanning data")
        count = 0

        # Invoice item data that may apply to multiple rows.
        item_resource_id = None
        item_row_type = 'DETAIL'
        item_unit_type = 'MONTH'
        item_start_date = None
        item_end_date = None
        item_charge_type = 'RECURRING'

        for line in self._engine.tabular_line_query().intersects_vertical_edge(
                invoice_item_left_edge).predicate(data_window).generate():
            logger.info("LINE: {0}".format(line))
            count += 1
            # Fetch out the rest of the row, excluding anything to the left
            # of this line.
            logger.info("QUERY Y-RANGE: s={0}, e={1}".format(line.region.top, line.region.bottom))
            row_items = self._engine.indexed_query()\
                .matches_type(tm.Line)\
                .page(line.page_number)\
                .y_range(line.region.top, line.region.bottom)\
                .is_to_right_of(line.region).order_by_left_edge()

            item_list = list(row_items)
            for item in item_list:
                logger.info("  ITEM: {0}".format(item.text))

            # Invoice items for a resource start with an initial line that
            # has a date in the first row item.  If we see this it's time
            # to look for the resource ID and also date range information.
            if len(item_list) > 1:
                candidate_start_date = data_utils.extract_date(item_list[0].text)
                if candidate_start_date is not None:
                    # This line starts an invoice group.  Start by looking
                    # for the resource id.
                    server_regex = r'^.*Server:\s*([A-Za-z0-9]+)\s*$'
                    server_match = re.match(server_regex, line.text)
                    if server_match:
                        item_resource_id = server_match.group(1)
                        logger.info(
                            "PARSING--resource id: {0}".format(item_resource_id))
                    else:
                        item_resource_id = None

                    # Next store the date range.
                    item_start_date = candidate_start_date
                    candidate_end_date = data_utils.extract_date(item_list[1].text)
                    if candidate_end_date is not None:
                        item_end_date = candidate_end_date
                        item_charge_type = 'RECURRING'
                    else:
                        onetime_regex = r'^\s*One Time'
                        if re.match(onetime_regex, item_list[1].text):
                            item_charge_type = 'ONE-TIME'
                        else:
                            item_charge_type = 'RECURRING'

                # Scan backwards to get the total and unit (actually monthly
                # price).
                item_total_amount = data_utils.extract_currency(item_list[-1].text)
                item_unit_amount = data_utils.extract_currency(item_list[-2].text)

                # OK let's make an invoice item and add it.
                invoice_item = InvoiceItem()
                invoice_item.item_row_type = item_row_type
                invoice_item.resource_id = item_resource_id
                invoice_item.description = line.text
                invoice_item.unit_amount = item_unit_amount
                invoice_item.unit_type = item_unit_type
                invoice_item.total_amount = item_total_amount
                invoice_item.currency = invoice.currency
                invoice_item.start_date = item_start_date
                invoice_item.end_date = item_end_date
                invoice_item.charge_type = item_charge_type
                if invoice_item.charge_type == 'ONE-TIME':
                    invoice_item.unit_type = 'OTHER'

                invoice.items.append(invoice_item)
            else:
                logger.warning("Unexpected text where invoice item should be: {0}".format(line))

        logger.info("Found {0} data lines".format(count))

        return invoice

    def _get_summary_total(self, label_regex):
        summary_tag_line = self._engine.tabular_line_query().matches_regex(
            label_regex).first()
        if summary_tag_line:
            summary_line = self._engine.tabular_line_query().page(
                summary_tag_line.page_number).is_to_right_of(
                summary_tag_line.region).matches_currency().first()
            logger.debug(summary_line)
            return data_utils.extract_currency(summary_line.text)
        else:
            return None
