#!/usr/bin/python3
# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

"""Invoice processing command line interface (CLI)"""

# Standard Python libraries. 
import argparse
import json
import logging
import os

# Coldstream invoice processing libraries. 
import ip_api
import ip_base

# Standard logging initialization.
logger = logging.getLogger(__name__)

#############################################################################
# Command classes
#############################################################################

# Command classes have a name, help string, and an execute method. 

class CommandRepoCreate(object):
    name = "repo-create"
    help = "Create a new repo from scratch"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])

    def execute(self, command_options):
        """Create a new repository with given root option"""
        args = self._parser.parse_args(command_options)
        repo = ip_base.Repo.create_repo(args.repo_cfg)
        print("Repo created: {0}".format(repo))


class CommandRepoDelete(object):
    name = "repo-delete"
    help = "Delete an existing repo"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--force", help="Delete without confirmation")

    def execute(self, command_options):
        """Delete an existing repo"""
        args = self._parser.parse_args(command_options)
        repo_cfg = args.repo_cfg
        if not args.force and not confirm(
                "Delete repo located at {0}".format(repo_cfg)):
            print("Cancelled")
            return 0
        ip_base.Repo.delete_repo(repo_cfg)
        print("Repo deleted: {0}".format(repo_cfg))


class CommandRepoShow(object):
    name = "repo-show"
    help = "Show repo configuration details"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--validate", help="Validate the repo")

    def execute(self, command_options):
        args = self._parser.parse_args(command_options)
        repo = ip_base.Repo(args.repo_cfg)

        # Show configuration contents.
        cfg = repo.get_cfg()
        for section in cfg.sections():
            print("[{0}]".format(section))
            for key, name in cfg.items(section):
                print("{0} = {1}".format(key, name))

        # Validate the repository if so requested.
        if args.validate == "True":
            print("Repo Validation Results")
            ok, messages = repo.validate()
            print("\n".join(messages))
            if ok:
                print("Validation Successful")
                return 0
            else:
                print("Validation Failed")
                return 1
        else:
            print("No validation requested")


class CommandInvoiceCreate(object):
    name = "invoice-create"
    help = "Add a new invoice to the repo"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--tenant", help="Name of tenant")
        self._parser.add_argument("--invoice", help="Location of invoice file")
        self._parser.add_argument("--name", help="Optional invoice name")

    def execute(self, command_options):
        args = self._parser.parse_args(command_options)
        api = ip_api.InvoiceApi(ip_base.Repo(args.repo_cfg))
        id = api.load_invoice(args.invoice, args.name)
        print("Invoice created: {0}".format(id))


class CommandInvoiceProcess(object):
    name = "invoice-process"
    help = "Scan invoice and derive semantic model"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--id", help="ID of single invoice to process")

    def execute(self, command_options):
        args = self._parser.parse_args(command_options)
        api = ip_api.InvoiceApi(ip_base.Repo(args.repo_cfg))
        id = api.process_invoice(args.id)
        print("Invoice processed: {0}".format(id))


class CommandInvoiceShow(object):
    name = "invoice-show"
    help = "Show contents of one or more invoices"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--id", help="ID of single invoice to show")
        self._parser.add_argument("--summary", dest='summary', action='store_true', help="Print summmary list only")
        self._parser.set_defaults(summary=False)

    def execute(self, command_options):
        args = self._parser.parse_args(command_options)
        api = ip_api.InvoiceApi(ip_base.Repo(args.repo_cfg))
        if args.id:
            invoice = api.get_invoice(args.id)
            if args.summary is True:
                self._print_invoice_summary(invoice)
            else:
                self._print_invoice_json(invoice)
        else:
            invoice_list = api.get_all_invoices()
            if args.summary == True:
                for invoice in invoice_list:
                    self._print_invoice_summary(invoice)
            else:
                self._print_invoice_json(invoice_list)

    def _print_invoice_summary(self, invoice):
        invoice_content = invoice.content
        print("INVOICE: id={0}".format(invoice.id))
        print("  identifer={0}".format(invoice_content.identifier))
        print("  vendor={0}".format(invoice_content.vendor))
        print("  effective_date={0}".format(invoice_content.effective_date))
        print("  total_amount={0}".format(invoice_content.total_amount))
        print("  currency={0}".format(invoice_content.currency))

    def _print_invoice_json(self, invoice_data):
        metadata_as_json = dump_swagger_object_to_json(invoice_data,
                                                       indent=2, sort_keys=True)
        print(metadata_as_json)

class CommandInvoiceValidate(object):
    name = "invoice-validate"
    help = "Validate an invoice emitting non-zero status if validation fails"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--id", help="ID of single invoice to validate")
        self._parser.set_defaults(summary=False)

    def execute(self, command_options):
        args = self._parser.parse_args(command_options)
        api = ip_api.InvoiceApi(ip_base.Repo(args.repo_cfg))
        all_match = api.validate_invoice(args.id)
        if all_match is True:
            print("All rule checks passed")
            return 0
        else:
            print("One or more rule checks failed")
            return 1

class CommandInvoiceDelete(object):
    name = "invoice-delete"
    help = "Delete an invoice"

    def __init__(self):
        self._parser = standard_command_parser(self.name, ["--repo-cfg"])
        self._parser.add_argument("--id", help="ID of invoice to delete")

    def execute(self, command_options):
        args = self._parser.parse_args(command_options)
        api = ip_api.InvoiceApi(ip_base.Repo(args.repo_cfg))
        metadata = api.delete_invoice(args.id)


#############################################################################
# Utility functions
#############################################################################

def confirm(message):
    yes_no_message = "{0} [yes/no]? ".format(message)
    while True:
        answer = input(yes_no_message).lower()
        if answer == "yes":
            return True
        elif answer == "no":
            return False
        else:
            print("Please enter yes or no")


def generate_command_help(commands):
    lines = ["commands:"]
    # Generate command formatting string by measuring sites of command strings.
    maxlen = 0
    for key in commands:
        maxlen = max(maxlen, len(key))
    format_string = "  {0:" + str(maxlen) + "s} {1}"

    # Print a line for each command that has been registered
    for key in sorted(commands):
        command_class = commands[key]
        line = format_string.format(command_class.name, command_class.help)
        lines.append(line)
    return "\n".join(lines)


def standard_command_parser(command_name, option_names):
    """Creates a command parser with 0 or more standard options"""
    std_parser = argparse.ArgumentParser(prog='ipctl.py',
                                         usage="%(prog)s {0} [options]".format(command_name))
    for option in option_names:
        if option == "--repo-cfg":
            std_parser.add_argument("--repo-cfg",
                                    help="Invoice repo.cfg location (default: %(default)s)",
                                    default=os.getenv("REPO_CFG"))
        else:
            raise Exception("Unknown option: {0}".format(option))

    return std_parser


def standard_options(parser, option_names):
    """Inserts standard options shared across multiple commands"""
    for option in option_names:
        if option == "--repo-cfg":
            parser.add_argument("--repo-cfg",
                                help="Invoice repo.cfg location (default: %(default)s)",
                                default=os.getenv("REPO_CFG"))
        else:
            raise Exception("Unknown option: {0}".format(option))


def dump_swagger_object_to_json(obj, indent=None, sort_keys=None):
    """Dumps a generated swagger object to JSON by supplying default to
    convert objects to dictionaries. 
    a dictionary"""
    converter_fn = lambda unserializable_obj: unserializable_obj.to_dict()
    return json.dumps(obj, indent=2, sort_keys=True, default=converter_fn)

def init_logging(log_level, log_file=None):
    """Validates log level and starts logging"""
    if log_level == "CRITICAL":
        level = logging.CRITICAL
    elif log_level == "ERROR":
        level = logging.ERROR
    elif log_level == "WARNING":
        level = logging.WARNING
    elif log_level == "INFO":
        level = logging.INFO
    elif log_level == "DEBUG":
        level = logging.DEBUG
    else:
        raise Exception("Unknown log level: " + log_level)

    if log_file is None:
        logging.basicConfig(level=level)
    else:
        logging.basicConfig(filename=log_file, level=level)

    logger.debug("Initializing log: log_file={0}, log_level={1}".format(log_file, log_level))


#############################################################################
# Command line processor
#############################################################################

# Define the command array. 
commands = {}


def addCommand(command):
    commands[command.name] = command


addCommand(CommandInvoiceCreate())
addCommand(CommandInvoiceProcess())
addCommand(CommandInvoiceDelete())
addCommand(CommandInvoiceShow())
addCommand(CommandInvoiceValidate())
addCommand(CommandRepoCreate())
addCommand(CommandRepoDelete())
addCommand(CommandRepoShow())

# Define top-level command line parsing.
parser = argparse.ArgumentParser(
    formatter_class=argparse.RawDescriptionHelpFormatter,
    description=generate_command_help(commands),
    epilog="For more information try -h on specific commands")
parser.add_argument("--log-level",
                        help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                        default=os.getenv("LOG_LEVEL", "INFO"))
parser.add_argument("--log-file",
                        help="Name of log file (default: %(default)s)",
                        default=os.getenv("LOG_FILE", None))
parser.add_argument("command", default=None)
parser.add_argument("command_options", nargs=argparse.REMAINDER)

# Process options.  This will automatically print help. 
global_args = parser.parse_args()

# Start logging.
init_logging(log_level=global_args.log_level, log_file=global_args.log_file)

# Dereference the command and locate command class or help. 
command = global_args.command
if command is None or command == "help":
    parser.print_help()
    exit(0)

command_class = commands.get(command)
if command_class is None:
    print("Unrecognized command: {0}".format(command))
    exit(1)

# Execute the command. 
rc = command_class.execute(global_args.command_options)
exit(rc)
