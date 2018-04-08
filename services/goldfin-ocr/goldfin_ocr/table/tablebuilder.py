#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions to transform scanned PDF invoice to a generic tabular model"""
import io
import json
import logging
from lxml import etree
import unittest
from . import tabularmodel as tm

# Define logger
logger = logging.getLogger(__name__)


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
            logger.debug("Found page: {0}".format(page_no))

        # Handle a block by processing tables and ignoring other structures. 
        elif next_elem.tag == 'block':
            in_block = next_elem
            block_type = in_block.get('blockType')
            logger.debug("Found block, blockType=" + block_type)
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
                        line = tm.Line()
                        line.region = tm.Region(page_number=page_no,
                                                left=in_line.get("l"),
                                                top=in_line.get("t"),
                                                right=in_line.get("r"),
                                                bottom=in_line.get("b"))
                        block.add_line(line)
                        str_value = ""
                        for in_formatting in in_line.iter('formatting'):
                            for in_char in in_formatting.iter('charParams'):
                                str_value = str_value + in_char.text
                        line.append_text(str_value)
                        logger.debug("Found block text: " + str_value)

            elif block_type == 'Table':
                table = tm.Table()
                page.add_table(table)
                logger.debug("Found table")

                # Process rows.
                row_num = 0
                for in_row in in_block.iter('row'):
                    row_num += 1
                    row = tm.Row()
                    table.add_row(row)
                    logger.debug("Found row: {0}".format(row_num))

                    # Iterate across cells, tracking columns. 
                    col_num = 0
                    for in_cell in in_row.iter('cell'):
                        col_num += 1
                        cell = tm.Cell(col_num)
                        logger.debug("Found cell: {0}".format(col_num))
                        row.add_cell(cell)
                        cell.height = int(in_cell.get("height"))
                        cell.width = int(in_cell.get("width"))

                        for in_text in in_cell.iter('text'):
                            for in_line in in_text.iter('line'):
                                line = tm.Line()
                                line.region = tm.Region(page_number=page_no,
                                                        left=in_line.get("l"),
                                                        top=in_line.get("t"),
                                                        right=in_line.get("r"),
                                                        bottom=in_line.get(
                                                            "b"))
                                cell.add_line(line)
                                str_value = ""
                                for in_formatting in in_line.iter(
                                        'formatting'):
                                    for in_char in in_formatting.iter(
                                            'charParams'):
                                        str_value = str_value + in_char.text
                                line.append_text(str_value)
                                logger.debug("Found cell text: " + str_value)
        # Ignore remaining tags.
        else:
            pass

    # Return the document. 
    return doc
