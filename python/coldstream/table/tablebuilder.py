#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Functions to transform scanned PDF invoice to a generic tabular model"""
import io
import sys
from lxml import etree
import tabularmodel as tm

def _set_location(page, target_tag, source_tag):
    """Set the tag page and pixel extent"""
    target_tag.set("page", page) 
    target_tag.set("l", source_tag.get("l"))
    target_tag.set("t", source_tag.get("t"))
    target_tag.set("r", source_tag.get("r"))
    target_tag.set("b", source_tag.get("b"))

def build_model(xml):
    """Parse ABBYY XML scan output into a tabular model"""
    # Load XML and strip namespaces. 
    xml_input = io.StringIO(xml)
    it = etree.iterparse(xml_input)
    for _, el in it:
        if '}' in el.tag:
            el.tag = el.tag.split('}', 1)[1]  # strip all namespaces
    root = it.root
 
    # Extract tabular data. 
    #print('<document>')
    doc = etree.Element("document")
    page_no = 0
    for next_elem in root.iter():
        # Handle a page tag by incrementing. 
        if next_elem.tag == 'page':
            page_no += 1
            page_no_str = str(page_no)
            #print("Found a page: {0}".format(page_no))
    
        # Handle a block by processing tables and ignoring other structures. 
        elif next_elem.tag == 'block':
            in_block = next_elem
            blockType = in_block.get('blockType')
            if blockType == 'Separator':
                continue
            elif blockType == 'SeparatorsBox':
                continue
            elif blockType == 'Table':
                #print('<table>')
                table = etree.SubElement(doc, "table")
                _set_location(page_no_str, table, in_block)
    
                # Process rows.  Set the row top to start at the top of the 
                # table. 
                row_num = 0
                row_t = int(table.get("t"))
                for in_row in in_block.iter('row'):
                    #print('<row>')
                    row = etree.SubElement(table, "row")
                    row_num += 1
           
                    # Rows do not have pixel location, so we derive it 
                    # from the cell sizes by incrementing across and down. 
                    row.set("page_num", page_no_str)
                    row.set("row_num", str(row_num))
                    row.set("l", table.get("l"))
                    row_l = int(table.get("l"))
                    row.set("t", str(row_t))
                    # Start with left and top and increment as we read cells.
                    row_r = row_l
                    row_b = row_t
    
                    # Iterate across cells, tracking columns. 
                    column = 0
                    for in_cell in in_row.iter('cell'):
                        #print('<cell>')
                        cell = etree.SubElement(row, "cell")
                        column += 1
    
                        # Let page, column, and left/top coordinates of cell.
                        cell.set("page_num", page_no_str)
                        cell.set("row_num", str(row_num))
                        cell.set("column_num", str(column))
                        cell.set("width", in_cell.get("width"))
                        cell.set("height", in_cell.get("height"))
                        cell_height = int(in_cell.get("height"))
                        cell_width = int(in_cell.get("width"))
    
                        # Left edge of cell is current right edge of row.
                        cell.set("l", str(row_r))
                        cell.set("t", str(row_t))
    
                        # Increment row bottom/right of row and use values to set
                        # cell bottom/right
                        row_r += cell_width
                        cell.set("r", str(row_r))
                        if (row_t + cell_height > row_b):
                            row_b = row_t + cell_height
                        cell.set("b", str(row_b))
    
                        #set_location(page_no_str, cell, in_cell)
                        for in_text in in_cell.iter('text'):
                            #print('<text>')
                            text = etree.SubElement(cell, "text")
                            #set_location(page_no_str, text, in_text)
                            for in_line in in_text.iter('line'):
                                for in_formatting in in_line.iter('formatting'):
                                    str_value = ""
                                    for in_char in in_formatting.iter('charParams'):
                                        str_value = str_value + in_char.text
                                    #print("<line>" + str + "</line>")
                                    etree.SubElement(text, "line").text = str_value
                    # At end of row, set the right/bottom coordinates and advance
                    # the row position. 
                    row.set("r", str(row_r))
                    row.set("b", str(row_b))
                    row_t = row_b 
    
            elif blockType == 'Text':
                #print('<text>')
                text = etree.SubElement(doc, "text")
                #set_location(page_no_str, text, in_block)
                for in_text in in_block.iter('text'):
                    for in_line in in_text.iter('line'):
                        for in_formatting in in_line.iter('formatting'):
                            str_value = ""
                            for in_char in in_formatting.iter('charParams'):
                                str_value = str_value + in_char.text
                            #print("<line>" + str + "</line>")
                            etree.SubElement(text, "line").text = str_value
            else:
                block = etree.SubElement(doc, "block", blockType = blockType)
                #print('<block blockType="' + blockType + '"/>')
    
        # Ignore remaining tags. 
        else:
            #print("--Ignored tag: {0}".format(next_elem.tag))
            continue
    
    # Print the result, nicely. 
    output = etree.tostring(doc, pretty_print=True).decode()
    print(output)
