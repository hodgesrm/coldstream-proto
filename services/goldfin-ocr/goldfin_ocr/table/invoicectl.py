#!/usr/bin/env python3
# Copyright (c) 2017-2018 Goldfin Systems LLC.  All rights reserved. 

"""Invoice processing command line interface (CLI)"""

# Standard Python libraries. 
import argparse
import logging
import os

from goldfin_ocr.table import tablebuilder as tb
from goldfin_ocr.table import pfactory
import goldfin_ocr.util as util

# Standard logging initialization.
logger = logging.getLogger(__name__)


def process_invoice(args):
    """Process a single request using command line arguments

    :param args: Argparse arguments
    """

    # Read in the XML from OCR and convert to tabular model.
    with open(args.xml, "rb") as xml_file:
        xml = xml_file.read()
    logger.info("Reading data: " + args.xml)
    model = tb.build_model(xml)
    if logger.isEnabledFor(logging.DEBUG):
        logger.debug(util.dump_to_json(model))

    # Select a provider.
    provider = pfactory.get_provider(model)
    if provider is None:
        raise Exception("Unable to find provider for document content")

    # Translate provider and print output.
    logger.info("Provider: " + provider.name())
    logger.info("Fetching invoice content")
    invoice = provider.get_content()
    json = util.dump_to_json(invoice)
    if args.out is None:
        print(json)
    else:
        print("Writing invoice output to {0}".format(args.out))
        with open(args.out, "w") as f:
            f.write(json)

#############################################################################
# Command line processor
#############################################################################

def exec_cmd():
    # Define top-level command line parsing.
    parser = argparse.ArgumentParser(prog='invoicectl.py',
                                     usage="%(prog)s [options]")
    parser.add_argument("--xml",
                        help="Document XML file input")
    parser.add_argument("--out",
                        help="Invoice JSON output file (default: stdout)",
                        default=None)
    parser.add_argument("--log-level",
                        help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                        default="INFO")
    parser.add_argument("--log-file",
                        help="Name of log file (default: %(default)s)",
                        default=os.getenv("LOG_FILE", "invoice.log"))

    # Process options.  This will automatically print help. 
    args = parser.parse_args()

    # Start logging.
    util.init_logging(log_level=args.log_level, log_file=args.log_file)

    # Process the document.
    process_invoice(args)

    print("Done!!!")

if __name__ == '__main__':
    exec_cmd()
