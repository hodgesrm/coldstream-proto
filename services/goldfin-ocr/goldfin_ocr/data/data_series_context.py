#!/usr/bin/python3
# Copyright (c) 2018 Robert Hodges.  All rights reserved.

"""Context with properties and utility methods to process an observation"""

import os.path
import logging

logger = logging.getLogger(__name__)


class DataSeriesContext(object):
    def __init__(self, data_series_id=None,
                 work_dir_path=None):
        """Creates a new context"""

        self.data_series_id = data_series_id
        self.work_dir_path = work_dir_path

    def write_work_file(self, content, name, local_logger=logger):
        """Writes out content to work directory to help with debugging

        :param content: Content data
        :type content: str
        :param name: file name to use in work directory
        :type: name: str
        :param local_logger: Logger to use for output message, defaults to logger of this class
        :type local_logger: logging.Logger
        """
        path = os.path.join(self.work_dir_path, name)
        local_logger.info(
            "Storing work file: data_series_id={0}, path={1}".format(
                self.data_series_id, path))
        with open(path, "w") as out_file:
            out_file.write(content)