# Copyright (c) 2018 Robert Hodges.  All rights reserved.

"""Leaseweb invoice analyzer"""

import logging
import re

from goldfin_ocr.api.models.invoice import Invoice
import goldfin_ocr.table.tabularquery as tq
import goldfin_ocr.table.tabularmodel as tm
import goldfin_ocr.data as data

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
        lines = engine.query().matches_type(tm.Line).page(1).matches_regex(
            r'www\.leaseweb\.com').generate()
        for line in lines:
            logger.debug(line)
            count += 1
        logger.debug("count of www.leaseweb.com: {0}".format(count))
        return count >= 1

    def name(self):
        return "LeaseWeb"

    def get_content(self):
        """Analyzes the invoice tabular model and returns semantic content"""

        # Extract invoice header information available from text blocks.
        invoice = Invoice()
        invoice.vendor = "LeaseWeb"

        # Find first page and define geometric positions of header info.
        page1 = self._engine.page_query(1).cut().first()
        identifier_region = tm.Region(left=856, top=360, right=1154,
                                      bottom=389)
        eff_date_region = tm.Region(left=856, top=410, right=1096, bottom=445)

        # Find identifier code.  Strip out invoice number tag in case OCR
        # glommed it in with identifier.
        identifier_line = self._engine.line_query(root=page1).intersects(
            identifier_region).first()
        identifier = identifier_line.text.strip()
        invoice.identifier = re.sub(r'^\s*Invoice number:\s*', '', identifier)

        # Find effective date.
        eff_date_line = self._engine.line_query(root=page1).intersects(
            eff_date_region).first()
        invoice.effective_date = data.extract_date(eff_date_line.text.strip())
        if invoice.effective_date is None:
            logger.warning("Unable to extract date: {0}".format(eff_date_line))

        # Currency has to be inferred from what we see on the first page:
        # Price total header shows Total(€), Total(US$), or Total(SG$).
        total_regex = r'^.*Total\s*\((.*)\).*$'
        total_header = self._engine.line_query(root=page1).matches_regex(
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

        invoice.total_amount = self._get_summary_total(r'^\s*Total Amount Due')
        invoice.subtotal_amount = self._get_summary_total(r'^\s*(Subtotal customer|Subtotal this invoice)')
        # Tax only shown on US invoices, it seems.
        invoice.tax = self._get_summary_total(r'^\s*Tax\s*\(US\$\)')
        return invoice

    def _get_summary_total(self, label_regex):
        summary_tag_line = self._engine.line_query().matches_regex(
            label_regex).first()
        if summary_tag_line:
            summary_line = self._engine.line_query().page(
                summary_tag_line.page_number).is_to_right_of(
                summary_tag_line.region).matches_currency().first()
            logger.debug(summary_line)
            return data.extract_currency(summary_line.text)
        else:
            return None