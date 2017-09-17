# Copyright (c) 2017 Robert Hodges.  All rights reserved.

"""OVH invoice analyzer"""

from datetime import datetime
import re
import uuid
import table.tabularmodel as tm
import ip_base
from generated.api.models.invoice_content import InvoiceContent
from generated.api.models.host_invoice_item import HostInvoiceItem

class OvhProcessor:
    """OVH Invoice Processor"""

    def __init__(self, tabular_model):
        self._tabular_model = tabular_model

    @staticmethod
    def conforms(model):
        """Returns True if we can find OVH identification within the
           invoice header."""

        def ovh_predicate(block):
            return (len(block.select_text(r'OVH\.com')) > 0)

        ovh_com_blocks = model.select_blocks(ovh_predicate)
        return len(ovh_com_blocks) == 1

    def name(self):
        return "OVH"

    def get_content(self):
        """Analyzes the invoice tabular model and returns semantic content"""

        # Extract invoice header information available from text blocks.
        # For OVH the sub-total is in the same table as the invoice item rows.
        id = str(uuid.uuid4())
        content = InvoiceContent(id=id, vendor="OVH.com")
        content.identifier = self._find_first_group_1(r'^Invoice:\s*(\S.*\S)\s*$')
        print(content.identifier)
        ovh_date = self._find_first_group_1(r'^Date:\s*(\S.*\S)\s*$')
        content.effective_date = self._get_normalized_date(ovh_date)
        print(content.effective_date)
        content.tax = 0.0
        print(content.tax)
        total = self._find_named_block_value(r'^TOTAL')
        if total is not None:
            total_amount, total_currency = self._get_amount_and_currency(total)
            content.total_amount = total_amount
            content.currency = total_currency
        print(content.total_amount)
        print(content.currency)
        content.hosts = []

        # Iterate across the invoice rows.
        for page in self._tabular_model.pages:
            for table in page.tables:
                if table.header_row().cells[0].matches_text("SUBSCRIPTION"):
                    for row in table.detail_rows():
                        if row.matches_text("SUB-TOTAL"):
                            # Great, found the sub total row.
                            amount, ignore = self._get_amount_and_currency(
                                row.cells[2].joined_text())
                            content.subtotal_amount = amount
                        else:
                            item = HostInvoiceItem()
                            item.resource_id = row.cells[1].joined_text()
                            item.total_amount, item.currency = self._get_amount_and_currency(
                                row.cells[3].joined_text())
                            content.hosts.append(item)

                            date_search = re.search(r'From (.*) to (.*)$',
                                                    row.cells[0].joined_text())
                            if date_search is not None:
                                item.start_date = self._get_normalized_date(date_search.group(1))
                                item.end_date = self._get_normalized_date(date_search.group(2))

                            unit_price_search = re.search(r'^([0-9]+) (\$.*)',
                                                          row.cells[2].joined_text())
                            if unit_price_search is not None:
                                item.unit_amount, ignore = self._get_amount_and_currency(
                                    unit_price_search.group(2))
                                item.units = int(unit_price_search.group(1))
                else:
                    raise Exception("Unrecognized table: "
                                    + table.header_row.joined_text(join_by="|"))

        return content

    def _find_named_block_value(self, select_value):
        """Find the value to the right of a named block"""
        def predicate(block):
            return len(block.select_text(select_value)) > 0

        blocks = self._tabular_model.select_blocks(predicate)
        if len(blocks) == 0:
            return None

        predicate_region = blocks[0].region
        def value_predicate(block):
            return (block.region.overlaps_vertically(predicate_region) and
                    block.region.is_to_right_of(predicate_region) and
                    block != blocks[0])
        value_blocks = self._tabular_model.select_blocks(value_predicate)
        if len(value_blocks) == 0:
            return None
        else:
            return value_blocks[0].text[0]

    def _find_first_group_1(self, regex):
        """Find group(1) of block text captured by regex"""

        # Find blocks that match.
        def predicate(block):
            return len(block.select_text(regex)) > 0
        blocks = self._tabular_model.select_blocks(predicate)
        if len(blocks) == 0:
            return None

        # Now run the regex again and return group(1) for anything that matches.
        groups = []
        for block in blocks:
            for text in block.text:
                match = re.match(regex, text)
                if match:
                    return match.group(1)

        return None

    def _get_amount_and_currency(self, text):
        matches = re.search(r'([0-9]*\.[0-9]*)\s*([A-Z]*)', text)
        if matches is None:
            return None, None
        else:
            return matches.group(1), matches.group(2)

    def _get_normalized_date(self, ovh_date):
        if ovh_date is None:
            return None
        else:
            return datetime.strptime(ovh_date,  '%B %d, %Y').strftime('%m-%d-%Y')