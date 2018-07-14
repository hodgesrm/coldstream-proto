# Shell script to add a client and prepare for operations. 

# Check prerequisites. 
if [ -z "$GOLDFIN_CONFIG_DIR" ]; then
  echo "Must set GOLDFIN_CONFIG_DIR"
  exit 1
fi

# Cd into build directory.  Necessary to make paths work. 
cd ../goldfin-admin-server/target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1

# Initialize the server. 
set -x
bin/svc-init create \
--init-params=$GOLDFIN_CONFIG_DIR/conf/init-params.yaml \
--dbms-config=$GOLDFIN_CONFIG_DIR/conf/dbms.yaml

# Create a tenant. 
bin/svc-client login --host localhost --user=sysadmin@system --password=secret12
bin/svc-client tenant-create --name 'skylineresearch.com' \
--description "Skyline Research, Inc." --schema-suffix=skyline
bin/svc-client tenant-list
# Create a user. 
bin/svc-client user-create --initialPassword=secret12 \
--user=test@skylineresearch.com 
