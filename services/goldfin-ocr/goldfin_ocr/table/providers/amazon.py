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


class AmazonProcessor:
    """Amazon Invoice Processor"""

    def __init__(self, tabular_model):
        self._tabular_model = tabular_model
        self._engine = tq.QueryEngine(self._tabular_model)

    @staticmethod
    def conforms(model):
        """Returns True if we can find 'Amazon Web Services, Inc.' at least
        once in the body of the first page
        """
        engine = tq.QueryEngine(model)
        count = 0
        lines = engine.tabular_query().matches_type(tm.Line).page(
            1).matches_regex(
            r'Amazon Web Services, Inc\.').generate()
        for line in lines:
            logger.debug(line)
            count += 1
        logger.debug("count of Amazon Web Services: {0}".format(count))
        return count >= 1

    def name(self):
        return vendors.AMAZON

    def get_content(self):
        """Analyzes the invoice tabular model and returns semantic content"""

        # Extract invoice header information available from text blocks.
        logger.info(util.dump_to_json(self._tabular_model))
        invoice = Invoice()
        invoice.vendor_identifier = vendors.AMAZON
        invoice.items = []

        # Find first page and define geometric positions of header info.
        page1 = self._engine.tabular_page_query(1).cut().first()
        identifier_region = tm.Region(left=1074, top=488, right=2378,
                                      bottom=516)
        eff_date_region = tm.Region(left=1074, top=537, right=2379, 
                                    bottom=568)

        # Find identifier code.  Strip out invoice number tag in case OCR
        # glommed it in with identifier.
        identifier_line = self._engine.tabular_line_query(
            root=page1).intersects(identifier_region).first()
        identifier = identifier_line.text.strip()
        invoice.identifier = re.sub(r'^\s*Invoice Number:\s*', '', identifier)

        # Find effective date.
        eff_date_line = self._engine.tabular_line_query(root=page1).intersects(
            eff_date_region).first()
        invoice.effective_date = data_utils.extract_date(eff_date_line.text.strip())
        if invoice.effective_date is None:
            logger.warning("Unable to extract date: {0}".format(eff_date_line))

        # Find account number. It's the numeric box under the 'Account number:'
        # Title.
        acct_regex = r'\s*[0-9]+'
        acct_title_regex = r'\s*Account\s*number:'
        acct_title = self._engine.tabular_line_query(
            root=page1).matches_regex(
            acct_title_regex).first()

        # Now look down to find it. 
        acct_region = tm.Region(left=acct_title.region.left,
            top=acct_title.region.bottom + 20, 
            right=acct_title.region.right,
            bottom=acct_title.region.bottom + 60)
        acct_line = self._engine.tabular_line_query(root=page1).intersects(
            acct_region).matches_regex(acct_regex).first()
        invoice.account = data_utils.extract_integer(acct_line.text.strip())

        # Most Amazon invoices use USD.  Confirm we see $ on first page
        # to assign currency. 
        dollar_regex = r'.*$'
        dollar_count = self._engine.tabular_line_query(
            root=page1).matches_regex(
            dollar_regex).count()
        if dollar_count > 0:
            invoice.currency = "USD"
        else:
            logger.warning(
                "Unable to determine currency for invoice: identifier={0}".format(
                invoice.identifier, total_header.text))

        # Get the date range.  It's on a line of the following form:
        # This invoice is for the billing period <MONTH> 1 - <MONTH> 30 , 2018
        date_prefix_regex = r'This invoice is for the billing period'
        date_range_regex = r'.* ([A-Za-z]+)\s*([0-9]+)\s*-\s*([A-Za-z]+)\s*([0-9]+)\s*,\s*([0-9]+)\s*$'
        date_range_line = self._engine.tabular_line_query(
            root=page1).matches_regex(date_prefix_regex).first()
        range_match = re.match(date_range_regex, date_range_line.text)
        if range_match:
            start_date_string = "{0} {1}, {2}".format(
                range_match.group(1), 
                range_match.group(2), 
                range_match.group(5))
            invoice_start_date = data_utils.extract_date(start_date_string)
            end_date_string = "{0} {1}, {2}".format(
                range_match.group(3), 
                range_match.group(4), 
                range_match.group(5))
            invoice_end_date = data_utils.extract_date(end_date_string)
        else:
            logger.error(
                "Unable to find date range: line={0}".format(date_range_line.text))

        # Total, subtotal, credits, and taxes are on the first page in the 
        # table-like object between "Summary" and "Total for this invoice".
        # We'll locate that region and then peel off values. 
        summary_upper_bound = self._engine.tabular_line_query(
            root=page1).matches_regex(r'.*Account Activity Page').first()
        summary_lower_bound = self._engine.tabular_line_query(
            root=page1).matches_regex(r'.*Total for this invoice').first()

        summary_region = summary_upper_bound.region.merge(summary_lower_bound.region)
        summary_lines = self._engine.tabular_line_query(
            root=page1).intersects(summary_region).matches_currency().generate()

        # We now have the relevant currency lines.  Extract each field. Note
        # that 'AWS Service Charges' does not show up on OCR, so we just get
        # a currency without text.  We ignore that line for now. 
        for line in summary_lines:
            if 'Charges' in line.text:
                invoice.subtotal_amount = data_utils.extract_currency(line.text)
            elif 'Credits' in line.text:
                invoice.credit = data_utils.extract_currency(line.text)
            elif 'Tax' in line.text:
                invoice.tax = data_utils.extract_currency(line.text)
            elif 'Total for this invoice' in line.text:
                invoice.total_amount = data_utils.extract_currency(line.text)
            else:
                logger.info("Ignored summary line: {0}".format(line.text))
 
        # That was just warming up.  Now we need to handle items.  To do this
        # we'll construct a frame that gives the bounds of the page that
        # contain invoice items.

        # Let's first establish a top bound on page 1 which is defined by the
        # bottom of the line that contains the words 'Detail' only.
        # We add extra pixels to ensure we're below it.
        extra_pixels = 5
        detail_line = self._engine.tabular_line_query().page(1).matches_regex(
                r'^\s*Detail\s*$').first()
        first_page_top_margin = detail_line.region.bottom + extra_pixels

        # The top margin for all other pages is 0.
        top_margin = 0

        # Get the bottom of the search range, which is above the legalese
        # at the bottom of each page.
        legal_regex = r'.*May include estimated US sales tax, VAT'
        legalese_line = self._engine.tabular_line_query().matches_regex(
                legal_regex).first()
        bottom_margin = legalese_line.region.top - extra_pixels

        # The left edge is 0 and the right edge is max. 
        left_margin = 0
        right_margin = 99999

        data_region = tm.Region(left=left_margin, top=top_margin,
                                right=right_margin, bottom=bottom_margin)

        # Log results of our discoveries.
        logger.info("First page: top={0}".format(first_page_top_margin))
        logger.info("Bounding data region: {0}".format(data_region))

        # We can now construct a predicate function that selects only
        # entities within this window.
        def data_window(entity):
            # Items above the invoice data on first page.
            if entity.page_number == 1 and entity.region.bottom < first_page_top_margin:
                logger.debug("BEFORE: {0}".format(entity))
                return False

            # Items that are outside the frame containing invoice data.
            if not data_region.contains(entity.region):
                logger.debug("OUTSIDE: {0}".format(entity))
                return False

            # If we get here this is something we need to look at.
            logger.debug("SELECTED: {0}".format(entity))
            return True

        # Iterate over the invoice line items.
        logger.info("Scanning data")
        count = 0

        # Invoice item data that may apply to multiple rows.
        invoice_item = None
        item_resource_id = None

        for line in self._engine.tabular_line_query()\
               .predicate(data_window).matches_currency()\
               .generate():
            logger.info("LINE: {0}".format(line))

            # Let's assign values based on what we find. 
            if 'Charges' in line.text:
                # This is the service amount. 
                invoice_item.subtotal_amount = data_utils.extract_currency(line.text)
            elif 'tax' in line.text:
                # This is the tax. 
                invoice_item.tax = data_utils.extract_currency(line.text)
            else:
                # This a new service. 
                if invoice_item is not None:                
                    invoice.items.append(invoice_item)

                invoice_item = InvoiceItem()
                invoice_item.item_row_type = 'SUMMARY'
                invoice_item.currency = invoice.currency
                invoice_item.total_amount = data_utils.extract_currency(line.text)
                invoice_item.start_date = invoice_start_date
                invoice_item.end_date = invoice_end_date
                # To get the description we need to strip out the currency.  
                invoice_item.description = re.sub(r'\s*\$[0-9. ]*', '', line.text)

        # Append the final invoice item and return. 
        if invoice_item is not None:
            invoice.items.append(invoice_item)
 
        return invoice
