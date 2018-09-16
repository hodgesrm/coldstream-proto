# Copyright (c) 2017-2018 Goldfin Systems LLC.  All rights reserved.

from datetime import datetime
import re
import logging

from goldfin_ocr.api.models.invoice import Invoice
from goldfin_ocr.api.models.invoice_item import InvoiceItem
import goldfin_ocr.table.tabularquery as tq
import goldfin_ocr.table.tabularmodel as tm
import goldfin_ocr.data_utils as data_utils
import goldfin_ocr.util as util
import goldfin_ocr.vendors as vendors
import goldfin_ocr.currencies as currencies

# Define logger
logger = logging.getLogger(__name__)


class InapProcessor:
    """INAP Invoice Processor using tabular query engine"""

    def __init__(self, tabular_model):
        self._tabular_model = tabular_model
        self._engine = tq.QueryEngine(self._tabular_model)
        # Find first page as multiple queries depend on it. 
        self._page1 = self._engine.tabular_page_query(1).cut().first()


    @staticmethod
    def conforms(model):
        """Returns True if we can find Internap identification within the
           invoice header."""

        engine = tq.QueryEngine(model)
        count = 0
        lines = engine.tabular_query().matches_type(tm.Line).page(
            1).matches_regex(
            r'Internap.*Corporation').generate()
        for line in lines:
            logger.debug(line)
            count += 1
        logger.debug("Count of Internap Corporation: {0}".format(count))
        return count >= 1

    def name(self):
        return vendors.INTERNAP

    def get_content(self):
        """Converts tabular model to Inap invoice"""
        invoice = Invoice()
        invoice.vendor_identifier = self.name()
        invoice.items = []

        # Find invoice identifier field.  Strip out 'Invoice Number:' tag to 
        # get the actual invoice identifier. 
        identifier_regex = r'^\s*Invoice\s*Number:\s*'
        identifier_line = self._engine.tabular_line_query(
            root=self._page1).matches_regex(identifier_regex).first()
        identifier = identifier_line.text.strip()
        invoice.identifier = re.sub(identifier_regex, '', identifier)
 
        # Find effective date.
        eff_date_regex = r'^\s*Invoice\s*Date:\s*'
        eff_date_line = self._engine.tabular_line_query(
            root=self._page1).matches_regex(eff_date_regex).first()
        eff_date = re.sub(eff_date_regex, '', eff_date_line.text.strip())
        invoice.effective_date = data_utils.extract_date(eff_date)
        if invoice.effective_date is None:
            logger.warning("Unable to extract date: {0}".format(eff_date_line))

        # Find the subtotal, taxes, and total, which follow similar patterns
        # on the left hand side of the first page. 
        invoice.currency = currencies.USD
        invoice.subtotal_amount = self._get_summary_total(
            r'\s*Se..ice\s*Items\s*')
        invoice.tax = self._get_summary_total(r'\s*Taxes\s*')
        invoice.total_amount = self._get_summary_total(
            r'\s*Invoice\s*Total\s*')

        # We now create a window of information to query for invoice items. 

        # Let's first establish a top bound on page 1 which is defined by the
        # bottom of the line that contains the words 'ID#' and 'Date Range'.
        # We add extra pixels to ensure we're below it.
        first_page = 1
        first_page_top = 0
        extra_pixels = 5
        for line in self._engine.tabular_line_query().page(1).matches_regex(
                '^.*(Service|Description)').generate():
            first_page_top = max(first_page_top, line.region.bottom + extra_pixels)

        # Similarly get the absolute bottom which is a above a line containing 
        # 'Invoice Total ... $ n,nnn.nn'. 
        last_page = 0
        last_page_bottom = 0
        for line in self._engine.tabular_line_query().matches_regex(
                r'Invoice Total').generate():
            price = self._engine.tabular_line_query().is_to_right_of(
                line.region).matches_currency().first();
            if price is not None:
                last_page = line.page_number
                last_page_bottom = max(last_page_bottom, line.region.top)

        # The top edge of pages is the bottom of any row containing the
        # phrase 'ID#'.  This appears in the first page
        # so we have to start low and work upwards. 
        top_margin_regex = '^ID#'
        top_margin = 99999
        for line in self._engine.tabular_line_query().matches_regex(
                top_margin_regex).generate():
            top_margin = min(top_margin, line.region.bottom)

        # The bottom edge of pages is above the 'Page X of Y' line.
        bottom_margin_regex = r'^Page [0-9]+ of [0-9]+'
        bottom_margin = 99999
        for line in self._engine.tabular_line_query().page(1).matches_regex(
                bottom_margin_regex).generate():
            bottom_margin = min(bottom_margin, line.region.top)
        bottom_margin += 10

        # Set the bounding data region.  We don't care about left or 
        # right as there is no external to cause confusion. 
        data_region = tm.Region(top=top_margin, bottom=bottom_margin)

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
        invoice_item_left_edge = 175
        logger.info("Scanning data")
        count = 0

        for line in self._engine.tabular_line_query().intersects_vertical_edge(
                invoice_item_left_edge).predicate(data_window).generate():
            logger.info("LINE: {0}".format(line))

            # Invoice items start with an initial line that has a numeric
            # ID number in the first column. 
            item_id = data_utils.extract_integer(line.text)
            # Fetch out the rest of the row, excluding anything to the left
            # of this line.
            logger.info("QUERY Y-RANGE: s={0}, e={1}".format(line.region.top, line.region.bottom))
            row_items = self._engine.indexed_query()\
                .matches_type(tm.Line)\
                .page(line.page_number)\
                .y_range(line.region.top, line.region.bottom)\
                .is_to_right_of(line.region).order_by_left_edge()
            row_item_list = list(row_items)

            if item_id is not None and len(row_item_list) >= 5:
                # This line starts an invoice item. 
                count += 1
                item = InvoiceItem()
                item.item_id = item_id
                for row_item  in row_item_list:
                    logger.debug("  ITEM: {0}".format(row_item.text))
 
                # Find all text in the 'Service Item' column then split
                # the resource ID from the rest of the description. 
                service_item_cell = self._engine.find_enclosing_cell(row_item_list[0])
                if service_item_cell is None:
                    resource_id, desc = self._get_resource(row_item_list[0].text)
                else:
                    service_item_text = ""
                    for line in service_item_cell.lines:
                        service_item_text += " " + line.text
                    resource_id, desc = self._get_resource(service_item_text.strip())
                item.resource_id = resource_id
                item.description = desc

                # Extract the start date, then get the end date from the 
                # next line in the cell.  If we can't find an end date we
                # assume it's a one-time charge.
                item.start_date = data_utils.extract_date(row_item_list[1].text)
                date_range_cell = self._engine.find_enclosing_cell(row_item_list[1])
                if date_range_cell is not None:
                    date_range_lines = date_range_cell.lines
                    if len(date_range_lines) > 1:
                        item.end_date = data_utils.extract_date(date_range_lines[1].text)
                item.one_time_charge = item.end_date is None

                # Remaining fields can be read out of their fields. 
                item.unit_amount = data_utils.extract_currency(row_item_list[2].text) 
                item.units = data_utils.extract_integer(row_item_list[3].text) 
                item.total_amount = data_utils.extract_currency(row_item_list[4].text) 
                item.currency = currencies.USD

                invoice.items.append(item)
            else:
                logger.warning("Unexpected text where invoice item ID should be: {0}".format(line))

        logger.info("Found {0} data lines".format(count))

        return invoice

    def _get_summary_total(self, label_regex):
        """Get a summary total from the first page

        We seek lines of form <label> $ <number> e.g. Invoice Total $ 5,734.00
        where the whitespace is variable and data cross multiple horizontal 
        cells.
        """
        summary_tag_line = self._engine.tabular_line_query(
            root=self._page1).matches_regex(label_regex).first()
        logger.debug("Summary tag line: {0}".format(summary_tag_line))
        if summary_tag_line:
            summary_line = self._engine.tabular_line_query().page(
                summary_tag_line.page_number).is_to_right_of(
                summary_tag_line.region).matches_currency().first()
            logger.debug("Summary line: {0}".format(summary_line))
            if summary_line is not None:
                return data_utils.extract_currency(summary_line.text)

        # If we get here the match failed.
        return None

    def _get_resource(self, text):
        """Split service item text into resource ID and description"""
        resource_search = re.search(r'^([a-z0-9_ .-]+) \(', text)
        if resource_search is not None:
            raw_id = resource_search.group(1)
            description = text[len(raw_id):]
            resource_id = raw_id.replace(' ', '')
            return resource_id, description.strip()
        else:
            return None, text.strip()
