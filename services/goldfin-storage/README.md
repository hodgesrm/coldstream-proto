# Storage README

This is an experimental storage daemon written in Python.  It is not being
developed further as it is more efficient to access S3 directly from the
Java API server. 

Install the code.

    pip install -e .

Run the storage daemon on port 5999

    export FLASK_DEBUG=true
    export FLASK_APP=daemon/storage_daemon.py
    export GOLDFIN_STORAGE_CONF=$PWD/storage.yaml
    flask run --host=0.0.0.0 --port=5999

Currently no build is necessary. 
