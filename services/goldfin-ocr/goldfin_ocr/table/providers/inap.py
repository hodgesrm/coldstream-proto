# Copyright (c) 2017 Robert Hodges.  All rights reserved.

from datetime import datetime
import re
import logging
import uuid
from decimal import Decimal
from goldfin_ocr.api.models.invoice import Invoice
from goldfin_ocr.api.models.invoice_item import InvoiceItem

# Define logger
logger = logging.getLogger(__name__)


class InapProcessor:
    """INAP Invoice Processor"""

    def __init__(self, tabular_model):
        self._tabular_model = tabular_model

    @staticmethod
    def conforms(model):
        """Returns True if we can find Internap identification within the
           invoice header."""

        def inap_predicate(block):
            return (len(block.select_text(r'Internap Corporation')) > 0)

        inap_blocks = model.select_blocks(inap_predicate)
        return len(inap_blocks) >= 1

    def name(self):
        return "Internap"

    def get_content(self):
        """Analyzes the invoice tabular model and returns adds semantic content"""

        # Extract invoice header information available from text blocks.
        # For OVH the sub-total is in the same table as the invoice item rows.
        invoice = Invoice()
        invoice.vendor_identifier = "Internap Corporation"

        invoice.identifier = self._find_first_group_1(
            r'.*Invoice\s*Number:\s*(\S*)\s*')
        logger.debug(invoice.identifier)

        invoice.effective_date = self._find_first_group_1(
            r'.* Invoice Date:\s*([0-9]+\-[0-9]+\-[0-9]+)\s*$')
        logger.debug(invoice.effective_date)

        # Find the invoice total.  This is tricky because it's the 5th line
        # of text in the block that is to the right of the block with
        # 'Invoice Total $' in it. ... OR ... it could be in the same block.
        def predicate(block):
            return len(block.select_text(r'.*Invoice Total \$')) > 0

        blocks = self._tabular_model.select_blocks(predicate)
        if len(blocks) > 0:
            # See if it's in this block.
            matching_text = blocks[0].select_text(r'.*Invoice Total\s*\$')
            if len(matching_text) > 0:
                invoice.total_amount, invoice.currency = self._get_amount_and_currency(matching_text[0])
            matching_text = blocks[0].select_text(r'.*Taxes\s*\$')
            if len(matching_text) > 0:
                invoice.tax, ignored = self._get_amount_and_currency(matching_text[0])

            if invoice.total_amount is None:
                # If we don't find it, look at the block to the right.
                predicate_region = blocks[0].region

                def value_predicate(block):
                    return (block.region.overlaps_vertically(predicate_region) and
                            block.region.is_to_right_of(predicate_region) and
                            block != blocks[0])

                value_blocks = self._tabular_model.select_blocks(value_predicate)
                if len(value_blocks) > 0:
                    logger.debug(value_blocks[0].joined_text())
                    invoice.total_amount, invoice.currency = self._get_amount_and_currency(
                        value_blocks[0].text[4])
                    invoice.tax, ignored = self._get_amount_and_currency(
                        value_blocks[0].text[3])
                    invoice.subtotal_amount, ignored = self._get_amount_and_currency(
                        value_blocks[0].text[2])

        # Set invoice items to an empty list.
        invoice.items = []

        # Iterate across all tables that have 'ID#' in the header row.
        # Iterate across the invoice rows.  Some items span more than one PDF table row
        # so we have a 'current_item' that allows work on items to span extra PDF
        # table rows.
        for page in self._tabular_model.pages:
            if logger.isEnabledFor(logging.DEBUG):
                logger.debug("Processing page: {0}".format(page.number))
            for table in page.tables:
                if table.header_row().cell_count() > 1 and \
                        table.header_row().cells[0].matches_text("ID#") and \
                        table.header_row().cells[1].matches_text(
                            "Service Items"):
                    if logger.isEnabledFor(logging.DEBUG):
                        logger.debug(
                            "Processing table: "
                            + table.header_row().joined_text(join_by="|"))
                    current_item = None
                    row_number = 0;
                    for row in table.detail_rows():
                        # This is a detail row, which may be spread across multiple PDF
                        # table rows.
                        row_number += 1
                        if logger.isEnabledFor(logging.DEBUG):
                            logger.debug("ROW[{0}]: {1}".format(row_number,
                                                                row.joined_text(
                                                                    "|")))

                        # If the first column does not have a number, it's a
                        # continuation line from a previous page.  For now we
                        # ignore it.
                        if row.cells[0].joined_text() == '':
                            continue

                        if current_item is None:
                            current_item = InvoiceItem()

                        # Try to fill in missing values.
                        if current_item.item_id is None:
                            current_item.item_id = row.cells[0].joined_text()
                        if current_item.resource_id is None:
                            current_item.resource_id = self._get_resource_id(
                                row.cells[1].joined_text())
                        if current_item.start_date is None:
                            current_item.start_date, current_item._end_date = self._get_date_range(
                                row.cells[2].joined_text())
                        if current_item.unit_amount is None:
                            current_item.unit_amount, ignored = self._get_amount_and_currency(
                                row.cells[3].joined_text())
                        if current_item.units is None:
                            current_item.units = row.cells[4].joined_text()
                        if current_item.total_amount is None:
                            current_item.total_amount, current_item.currency = self._get_amount_and_currency(
                                row.cells[5].joined_text())

                        # If we have everything post to the invoice and clear current item.
                        logger.debug("POST!!!")
                        invoice.items.append(current_item)
                        current_item = None

                else:
                    logger.debug("Ignored table: "
                                 + table.header_row().joined_text(join_by="|"))

        # Cross check and return content.
        total = Decimal('0.0')
        for item in invoice.items:
            if item.total_amount is not None:
                total += Decimal(item.total_amount)

        logger.info(
            "TOTAL: {0}  CHECKED_TOTAL: {1}".format(invoice.total_amount,
                                                    total))
        return invoice

    def _find_first_group_1(self, regex):
        """Find group(1) of block text captured by regex"""

        # Find blocks that match.
        def predicate(block):
            return len(block.select_text(regex)) > 0

        blocks = self._tabular_model.select_blocks(predicate)
        if len(blocks) == 0:
            logger.debug("NOT FOUND")
            return None

        # Now run the regex again and return group(1) for anything that matches.
        groups = []
        for block in blocks:
            for text in block.text:
                logger.debug("MATCHING: " + text)
                match = re.match(regex, text)
                if match:
                    return match.group(1)

        return None

    def _get_resource_id(self, text):
        resource_search = re.search(r'^([a-z0-9 .-]+com) \(', text)
        if resource_search is not None:
            return resource_search.group(1)
        else:
            return text

    def _get_date_range(self, text):
        date_search = re.search(
            r'^([0-9]+\-[0-9]+\-[0-9]+).\s*([0-9]+\-[0-9]+\-[0-9]+)$',
            text)
        if date_search is not None:
            return date_search.group(1), date_search.group(2)
        else:
            return None, None

    def _get_amount_and_currency(self, text):
        """Return amount and currency, removing commas from printed numbers"""
        match_usd = re.search(r'^.*\$\s*([0-9,]*\.[0-9]*)\s*', text)
        if match_usd:
            return match_usd.group(1).replace(',', ''), "USD"

        match_amount = re.search(r'^\s*([0-9,]*\.[0-9]*)\s*', text)
        if match_amount:
            return match_amount.group(1).replace(',', ''), "USD"
        else:
            return None, None
