import os
import sys
import logging
# print (sys.path)
# print (os.getcwd())
#print("__name__={0}, __package__={1}".format(__name__, __package__))

import connexion
from goldfin_storage.encoder import JSONEncoder
from goldfin_storage import config
from goldfin_storage import s3

# Standard logging initialization.
logger = logging.getLogger(__name__)
print("Running....")

# Set up the connexion app, which is a wrapper around a Flask app.
def configure_flask():
    # Find the storage daemon configuration file.
    logging.basicConfig(level="INFO")
    logger.info("Configuring storage server")

    # Ensure we have a configuration file for storage.
    storage_config_file = os.environ['GOLDFIN_STORAGE_CONF']
    logger.info("Storage configuration file: {0}".format(storage_config_file))
    if not os.path.exists(storage_config_file):
        raise Exception(
            "ERROR: storage configuration definition file missing or unreadable: {0}"
                .format(storage_config_file))

    logger.info("Configuring flask/connexion")
    flask_app = connexion.App(__name__,
                              specification_dir='./goldfin_storage/swagger/')
    #flask_app.app.config.from_envvar('FLASKR_SETTINGS', silent=True)
    flask_app.app.config.update(dict(
        GOLDFIN_STORAGE_CONF=storage_config_file
    ))
    flask_app.app.json_encoder = JSONEncoder
    flask_app.add_api('swagger.yaml', arguments={
        'title': 'REST API for Goldfin Storage Microservice'})

    return flask_app.app


# Declare Flask app so 'flask run' can find it.
app = configure_flask()