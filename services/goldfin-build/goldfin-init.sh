# Shell script to add a client and prepare for operations. 

# Cd into build directory.  Necessary to make paths work. 
cd ../goldfin-admin-server/target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1

# Initialize the server. 
set -x
bin/svc-init create --init-params=conf/init-params.sample.yaml \
 --dbms-config=conf/dbms.yaml

# Create a tenant. 
bin/svc-client login --host localhost --user=sysadmin@system --password=secret12
bin/svc-client tenant-create --name 'skylineresearch.com' \
 --description "Skyline Research, Inc." --schema-suffix=skyline
bin/svc-client tenant-list
# Create a user. 
bin/svc-client user-create --initialPassword=secret12 \
 --user=test@skylineresearch.com 
