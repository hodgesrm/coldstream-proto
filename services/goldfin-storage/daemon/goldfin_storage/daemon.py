# Copyright (c) 2017 Robert Hodges.  All rights reserved. 

import argparse
import logging
import os
import connexion
from .encoder import JSONEncoder
from . import config
from . import s3

# Standard logging initialization.
logger = logging.getLogger(__name__)


class Daemon(object):
    def __init__(self):
        self._parser = argparse.ArgumentParser(prog='goldfin_storage',
                                               usage="python -m %(prog)s [options]")
        self._parser.add_argument("--cfg",
                                  help="Config file location (default: %(default)s)",
                                  default=os.getenv("DAEMON_CFG"))
        self._parser.add_argument("--host",
                                  help="Host (default: %(default)s)",
                                  default="127.0.0.1")
        self._parser.add_argument("--port",
                                  help="Port (default: %(default)s)",
                                  default="4999")
        self._parser.add_argument("--log-level",
                                  help="CRITICAL/ERROR/WARNING/INFO/DEBUG (default: %(default)s)",
                                  default=os.getenv("DAEMON_LOG_LEVEL", "INFO"))
        self._parser.add_argument("--log-file",
                                  help="Name of log file (default: %(default)s)",
                                  default=os.getenv("DAEMON_LOG_FILE", None))

    def run(self):
        """Starts the daemon"""
        # Parse args. 
        args = self._parser.parse_args()

        # Start logging. 
        self._init_logging(log_level=args.log_level, log_file=args.log_file)

        # Load configuration information.
        logger.info("Loading configuration: {0}".format(args.cfg))
        config.load(args.cfg)

        # Start the S3 server.
        s3.initialize(config.global_config)

        # Start the REST API. 
        app = connexion.App(__name__, specification_dir='./swagger/')
        app.app.json_encoder = JSONEncoder
        app.add_api('swagger.yaml', arguments={'title': 'REST API for Goldfin Storage Microservice'})
        app.run(host=args.host, port=int(args.port), debug=True)

    def _init_logging(self, log_level, log_file=None):
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


print("Name: {0}".format(__name__))
if __name__ == '__main__':
    Daemon().run()
