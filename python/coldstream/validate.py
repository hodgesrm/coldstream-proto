# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Rules for validating invoices"""

from decimal import Decimal


class Rule:
    """Base class for all rules"""
    INVOICE = "invoice"
    INVOICE_ITEM = "invoice_item"

    def __init__(self, name=None, description=None, entity_type=None):
        """Initializes with repo settings"""
        self.name = name
        self.description = description
        self.entity_type = entity_type


class ValidationRule(Rule):
    """A validation rule that checks a condition and returns a boolean"""

    def __init__(self, name=None, description=None, entity_type=None, validator=None):
        """Initializes with repo settings"""
        super().__init__(name, description, entity_type)
        self.validator = validator

    def validate(self, entity):
        """Check condition and return tuple consisting of boolean, explanation"""
        return self.validator(entity)


class RuleSet:
    """A set of rules for invoice entities"""

    def __init__(self):
        self.rules = {}
        self.rules[Rule.INVOICE] = []
        self.rules[Rule.INVOICE_ITEM] = []

    def add_entity_rule(self, rule):
        self.rules[rule.entity_type].append(rule)

    def get_entity_rules(self, entity_type):
        return self.rules[entity_type]


# Invoice rules go here.
def invoice_total_check(invoice_content):
    """Ensure invoice total matches sum of invoice item totals"""
    item_total = Decimal('0.0')
    for item in invoice_content.hosts:
        item_total += Decimal(item.total_amount)

    matches = (Decimal(invoice_content.total_amount) == item_total)
    explanation = "Invoice total: {0}  Invoice item total: {1}".format(
        invoice_content.total_amount, item_total)
    return matches, explanation


def invoice_vendor_check(invoice_content):
    """Ensure invoice vendor is set"""
    vendor = invoice_content.vendor
    matches = (vendor is not None)
    explanation = "Vendor name: {0}".format(vendor)
    return matches, explanation


# Invoice item rules go here.
def invoice_item_total_check(invoice_item):
    """Ensure invoice total is unit_price * units"""
    if invoice_item.total_amount is None or invoice_item.units is None or invoice_item.unit_amount is None:
        return False, "Invoice item total={0}, units={1}, unit_amount={2}".format(
            invoice_item.total_amount, invoice_item.units, invoice_item.unit_amount
        )
    else:
        computed_total = Decimal(invoice_item.units) * Decimal(invoice_item.unit_amount)
        matches = (Decimal(invoice_item.total_amount) == computed_total)
        explanation = "Invoice item total: {0}  Computed total: {1}".format(
            invoice_item.total_amount, computed_total)
        return matches, explanation


def invoice_rule_set():
    ruleset = RuleSet()
    ruleset.add_entity_rule(ValidationRule("Invoice Total Check",
                                           "Invoice total equals sum of line item totals",
                                           Rule.INVOICE, invoice_total_check))
    ruleset.add_entity_rule(ValidationRule("Invoice Vendor Check",
                                           "Invoice has a vendor name",
                                           Rule.INVOICE, invoice_vendor_check))
    ruleset.add_entity_rule(ValidationRule("Invoice Item Total Check",
                                           "Invoice item total equals unit_amount X units",
                                           Rule.INVOICE_ITEM, invoice_item_total_check))
    return ruleset
