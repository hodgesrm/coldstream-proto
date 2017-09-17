# Copyright (c) 2017 Robert Hodges.  All rights reserved.
import table.tabularmodel as tm
import ip_base

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
        return len(inap_blocks) == 1

    def name(self):
        return "Internap"

    def interpret(self):
        """Analyzes the invoice tabular model and returns an Invoice instance"""
        raise ip_base.BaseError("Not implemented")