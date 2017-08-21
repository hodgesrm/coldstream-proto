from lxml import etree
import re

class OvhProcessor:
    """OVH Invoice Processor""" 
    def __init__(self, tabular_xml):
        self.tabular_xml = tabular_xml

    def conforms(self):
        """Returns True if we can find OVH identification within the
           invoice header."""
        xml = etree.parse(self.tabular_xml)
        document = xml.getroot()
        #print(root)

        scanned_lines = 0
        for text in document.iter("text"):
            print("Found text")
            for line in text.iter("line"):
                scanned_lines += 1
                #print(line.text)
                inap_tag_match = re.match("OVH.com", line.text)
                if inap_tag_match:
                    return True
                if scanned_lines > 20:
                    return False
        return False
