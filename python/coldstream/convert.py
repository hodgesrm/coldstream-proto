# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Rules for converting invoices to different representations"""

from decimal import Decimal
from datetime import datetime, timedelta
import generated.api.models as models


# Generators to iterate across invoices in interesting ways.
def invoice_items_generator(invoice: models.Invoice):
    """Emits invoice item rows with enclosing invoice information"""
    # Emit a header row with names. 
    yield _header()

    for item in invoice.items:
        item_row = [
            invoice.identifier,
            invoice.effective_date,
            invoice.vendor,
            invoice.subtotal_amount,
            invoice.tax,
            invoice.total_amount,
            item.item_id,
            item.resource_id,
            item.unit_amount,
            item.units,
            item.total_amount,
            item.currency,
            item.start_date,
            item.end_date]

        yield item_row


def invoice_items_daily_generator(invoice: models.Invoice):
    """Emits invoice item rows split into daily increments to enable
    cost accounting down to level of individual days"""
    # Emit a header row with names.
    yield _header()

    for item in invoice.items:
        start_date = datetime.strptime(item.start_date, '%Y-%m-%d')
        end_date = datetime.strptime(item.end_date, '%Y-%m-%d')
        interval = end_date - start_date
        days = interval.days
        one_day = timedelta(days=1)

        for day in range(days):
            # Compute single day date range
            today_start = start_date + timedelta(days=day)
            today_end = today_start + one_day
            item_row = [
                invoice.identifier,
                invoice.effective_date,
                invoice.vendor,
                invoice.subtotal_amount,
                invoice.tax,
                invoice.total_amount,
                item.item_id,
                item.resource_id,
                _decimal_value(item.unit_amount) / days,
                item.units,
                _decimal_value(item.total_amount) / days,
                item.currency,
                today_start,
                today_end]

            yield item_row


def _header():
    header = [
        'invoice.identifier',
        'invoice.effective_date',
        'invoice.vendor',
        'invoice.subtotal_amount',
        'invoice.tax',
        'invoice.total_amount',
        'item.item_id',
        'item.resource_id',
        'item.unit_amount',
        'item.units',
        'item.total_amount',
        'item.currency',
        'item.start_date',
        'item.end_date'
    ]
    return header


def _decimal_value(number):
    try:
        return Decimal(number)
    except:
        return 0.0


def csv_output_generator(invoice: models.Invoice, generator):
    """Return a sequence of CSV lines"""
    for items in generator(invoice):
        row = []
        for item in items:
            if item is None:
                row.append('""')
            else:
                row.append('"' + str(item) + '"')

        yield ", ".join(row)
