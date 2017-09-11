#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions to transform scanned PDF invoice to a generic tabular model"""
import io
import json
from lxml import etree
import unittest
import table.tabularmodel as tm


def _create_region(page_number, source_tag):
    """Return region that contains page and pixel coordinates of a location"""
    return tm.Region(int(page_number), int(source_tag.get("l")),
                     int(source_tag.get("t")), int(source_tag.get("r")),
                     int(source_tag.get("b")))


def build_model(xml):
    """Parse ABBYY XML scan output into a tabular model"""
    # Load XML and strip namespaces. 
    xml_input = io.BytesIO(xml)
    it = etree.iterparse(xml_input)
    for _, el in it:
        if '}' in el.tag:
            el.tag = el.tag.split('}', 1)[1]  # strip all namespaces
    root = it.root

    # Extract tabular data. 
    doc = tm.TabularModel()
    page_no = 0
    page = None
    for next_elem in root.iter():
        # Handle a page tag by incrementing. 
        if next_elem.tag == 'page':
            page_no += 1
            page = tm.Page(page_no)
            doc.add_page(page)
            print("Found page: {0}".format(page_no))

        # Handle a block by processing tables and ignoring other structures. 
        elif next_elem.tag == 'block':
            in_block = next_elem
            block_type = in_block.get('blockType')
            print("Found block, blockType=" + block_type)
            if block_type == 'Separator':
                continue
            elif block_type == 'SeparatorsBox':
                continue
            elif block_type == 'Text':
                block = tm.TextBlock()
                page.add_block(block)
                block.region = _create_region(page_no, in_block)

                for in_text in in_block.iter('text'):
                    for in_line in in_text.iter('line'):
                        for in_formatting in in_line.iter('formatting'):
                            str_value = ""
                            for in_char in in_formatting.iter('charParams'):
                                str_value = str_value + in_char.text
                            block.add_text(str_value)
                            print("Found text: " + str_value)

            elif block_type == 'Table':
                table = tm.Table()
                page.add_table(table)
                print("Found table")

                # Process rows.
                row_num = 0
                for in_row in in_block.iter('row'):
                    row_num += 1
                    row = tm.Row(row_num)
                    table.add_row(row)
                    print("Found row: {0}".format(row_num))

                    # Iterate across cells, tracking columns. 
                    col_num = 0
                    for in_cell in in_row.iter('cell'):
                        col_num += 1
                        cell = tm.Cell(col_num)
                        print("Found cell: {0}".format(col_num))
                        row.add_cell(cell)
                        cell.height = int(in_cell.get("height"))
                        cell.width = int(in_cell.get("width"))

                        for in_text in in_cell.iter('text'):
                            for in_line in in_text.iter('line'):
                                for in_formatting in in_line.iter('formatting'):
                                    str_value = ""
                                    for in_char in in_formatting.iter('charParams'):
                                        str_value = str_value + in_char.text
                                    cell.add_text(str_value)
                                    print("Found text: " + str_value)
        # Ignore remaining tags. 
        else:
            # print("Found " + next_elem.tag)
            pass

    # Return the document. 
    return doc


class TableBuilderTest(unittest.TestCase):
    ovh_xml = "/home/rhodges/coldstream/abbyy/code/ocrsdk.com/Bash_cURL/processing/ovh/invoice_WE666184.xml"
    inap_xml = "/home/rhodges/coldstream/abbyy/code/ocrsdk.com/Bash_cURL/processing/inap/Invoice-14066-486955.xml"

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_build_1_small(self):
        """Validate that we can set up a model from a single-page invoice from OVH"""
        with open(self.ovh_xml, "rb") as xml_file:
            xml = xml_file.read()

        # print(xml)
        model = build_model(xml)
        self.assertIsNotNone(model)

        # Ensure we find a block with OVH.com in it.
        def ovh_predicate(block):
            return (len(block.select_text(r'OVH\.com')) > 0)

        ovh_com_blocks = model.select_blocks(ovh_predicate)
        self.assertTrue(len(ovh_com_blocks) == 1)
        print(self._dump_to_json(ovh_com_blocks))

        # Ensure we can find the total by finding a block with "TOTAL" then
        # finding a block that overlaps horizontally.
        def total_predicate(block):
            return (len(block.select_text(r'^TOTAL$')) > 0)

        total_blocks = model.select_blocks(total_predicate)
        print(self._dump_to_json(total_blocks))
        self.assertTrue(len(total_blocks) == 1)
        total_region = total_blocks[0].region

        def total_value_predicate(block):
            return (block.region.overlaps_vertically(total_region) and
                    block != total_blocks[0])

        total_value_blocks = model.select_blocks(total_value_predicate)
        print(self._dump_to_json(total_value_blocks))
        self.assertTrue(len(total_blocks) == 1)

        # print(self._dump_to_json(model))

    def test_build_2_large(self):
        """Validate that we can set up a model from a multi-page invoice from Internap"""
        with open(self.inap_xml, "rb") as xml_file:
            xml = xml_file.read()
        print("Reading data " + self.inap_xml)

        # print(xml)
        model = build_model(xml)
        self.assertIsNotNone(model)
        print(self._dump_to_json(model))

        # Ensure we find a block with Internap Corporation in it.
        def inap_predicate(block):
            return (len(block.select_text(r'Internap Corporation')) > 0)

        inap_blocks = model.select_blocks(inap_predicate)
        self.assertTrue(len(inap_blocks) == 1)
        print(self._dump_to_json(inap_blocks))

        # Ensure we can find the total by finding a block with "Invoice Total"
        # then finding a block that overlaps horizontally.
        def total_predicate(block):
            return (len(block.select_text(r'^Invoice Total')) > 0 and block.region.page_number == 1)

        total_blocks = model.select_blocks(total_predicate)
        print(self._dump_to_json(total_blocks))
        self.assertTrue(len(total_blocks) == 1)
        total_region = total_blocks[0].region

        def total_value_predicate(block):
            return (block.region.overlaps_vertically(total_region) and
                    block != total_blocks[0] and
                    block.region.is_to_right_of(total_region))

        total_value_blocks = model.select_blocks(total_value_predicate)
        print(self._dump_to_json(total_value_blocks))
        self.assertTrue(len(total_value_blocks) == 1)

    def _dump_to_json(self, obj, indent=2, sort_keys=True):
        """Dumps a object to JSON by supplying default to
        convert objects to dictionaries.
        """
        converter_fn = lambda unserializable_obj: unserializable_obj.__dict__
        return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)


if __name__ == '__main__':
    unittest.main()
