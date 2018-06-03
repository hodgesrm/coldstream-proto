#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Tests ability to issue queries on tabular data"""
import json
import logging
import unittest

import goldfin_ocr.data_utils as data

# Define logger
logger = logging.getLogger(__name__)


class DataTest(unittest.TestCase):
    def setUp(self):
        pass

    def test_dates_YYYY_dash_MM_dash_DD(self):
        """YYYY-MM-DD format converts to same format"""
        raw_dates = ["2018-01-30", "2018-03-01", " 2017-08-08", "2019-12-15 "]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_dates_MMM_DD_YYYY(self):
        """MMM_DD_YYYY converts to YYYY-MM-DD format"""
        raw_dates = ["JAN 30, 2018", "MAR1,2018", " AUG 8 , 2017 ",
                     "Effective date: DEC 15, 2019"]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_dates_DD_MMM_YYYY(self):
        """DD_MMM_YYYY converts to YYYY-MM-DD format"""
        raw_dates = ["30JAN2018", "1 MAR 2018", " 08 AUG2017 ",
                     "Effective date: 15 DEC 2019"]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_dates_DD_fullmonth_YYYY(self):
        """DD_fullmonth_YYYY converts to YYYY-MM-DD format"""
        raw_dates = ["30 JANUARY 2018", "1 March 2018", " 08 August 2017 ",
                     "Effective date: 15 December 2019"]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_dates_DD_dash_MM_dash_YYYY(self):
        """DD-MM-YYYY converts to YYYY-MM-DD format"""
        raw_dates = ["30-1-2018", "1-3-2018", "08-08-2017 ",
                     "Effective date: 15-12-2019"]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_dates_MM_slash_DD_slash_YYYY(self):
        """MM/DD/YYYY converts to YYYY-MM-DD format"""
        raw_dates = ["1/30/2018", "3/1/2018", "08/08/2017 ",
                     "Effective date: 12/15/2019"]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_dates_MM_slash_DD_slash_YY(self):
        """MM/DD/YY converts to YYYY-MM-DD format"""
        raw_dates = ["1/30/18", "3/1/18", "08/08/2017 ",
                     "Effective date: 12/15/2019"]
        normalized_dates = ["2018-01-30", "2018-03-01", "2017-08-08",
                            "2019-12-15"]
        for raw, normalized in zip(raw_dates, normalized_dates):
            converted_date = data.extract_date(raw)
            self.assertEqual(normalized, converted_date, raw)

    def test_currency_values(self):
        """Verify we can extract currency values"""
        raw_currencies = ["$25,001.59 USD", "Total: 9,95€", "59.501,39",
                     "0.00", "0,00"]
        normalized_currencies = ["25001.59", "9.95", "59501.39",
                            "0.00", "0.00"]
        for raw, normalized in zip(raw_currencies, normalized_currencies):
            converted = data.extract_currency(raw)
            self.assertEqual(normalized, converted, raw)

    def test_integer_values(self):
        """Verify we can extract integer values"""
        raw_integers = ["25", " 995 ", "5950l", "0"]
        normalized_integers = [25, 995, 59501, 0]
        for raw, normalized in zip(raw_integers, normalized_integers):
            converted = data.extract_integer(raw)
            self.assertEqual(normalized, converted, raw)
