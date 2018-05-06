# Shell script to remove service data. 

# Cd into build directory.  Necessary to make paths work. 
cd ../goldfin-admin-server/target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1

# Initialize the server. 
set -x
bin/svc-init remove --init-params=conf/init-params.sample.yaml
