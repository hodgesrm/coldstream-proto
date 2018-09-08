#/bin/bash
# Clean up dev directories and files. 
find . -name __pycache__ -exec rm -r {} \;
rm -r dist
rm -r goldfin_client.egg-info
rm data_collector.log
