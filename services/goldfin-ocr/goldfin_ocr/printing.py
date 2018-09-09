# Copyright (c) 2017-2018 Goldfin Systems LLC.  All rights reserved. 

"""PDF printing functions
 
For this module to work you must install the cups-pdf driver on the host.
(`sudo apt install cups-pdf` does the trick on Ubuntu.) 
"""    

import cups
import logging
import os
import time
import uuid

# Define logger
logger = logging.getLogger(__name__)

def pdf_printer():
    """Creates a PDF printing driver or returns None if not available"""
    # Find the PDF printer using cups connection.
    conn = cups.Connection()
    printers = conn.getPrinters();
    pdf = printers.get('PDF')
    if pdf is None:
        return None
    else:
        return PdfPrinter(conn, pdf)
   
class PdfPrinter:
    """Manages PDF document printing using Linux CUPS PDF printer"""

    def __init__(self, connection, properties):
        """Initialize the printer driver

        :param connection: Connection for printer requests
        :type properties: cups.Connection
        :param properties: Dictionary of printer properties from cups
        :type properties: dict
        """
        self.connection = connection
        self.properties = properties

    def print(self, file_to_print, dest_path):
        """Prints a PDF document to a new file specified by caller
    
        :param file_to_print: Path of file to be printed
        :type input_path: str
        :param dest_path: Path to which file should be copied on completion
        :type dest_path: str
        :return: None
        """
        # Print the file using a job name to ensure a unique output name.
        if not os.path.exists(file_to_print):
            raise Exception("File not found: {0}".format(file_to_print))
        temp_name = str(uuid.uuid4()) + ".pdf"
        logger.info("Printing PDF: path={0}, temp_name={1}, dest_path={2}".format(
            file_to_print, temp_name, dest_path))
        self.connection.printFile('PDF', file_to_print, temp_name, {})
        
        # Wait for the file to appear in $HOME/PDF.
        output_path = os.path.join(os.getenv('HOME'), 'PDF', temp_name)
        logger.info("Awaiting PDF file: output_path={0}".format(output_path))
        for i in range(1, 30):
            if os.path.exists(output_path):
                # Sleep 2 seconds to ensure file is complete and flushed.
                time.sleep(2)
                os.rename(output_path, dest_path)
                logger.info("Wrote printed file: dest_path={0}".format(dest_path))
                return
            else:
                time.sleep(1)

        # If we get here printing didn't work. 
        raise Exception("Unable to print: driver=PDF, path={0}".format(file_to_print))
