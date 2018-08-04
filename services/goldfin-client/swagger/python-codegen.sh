#!/bin/sh
# Generate python code for Swagger model.
# (Source for this script: $CODEGEN/bin/python-petstore.sh)

CODEGEN=$HOME/git/swagger-codegen
SCRIPT="$0"
SCRIPT_DIR=`dirname $SCRIPT`
CLIENT_HOME=`cd $SCRIPT_DIR/..;pwd`
CLIENT_CODEGEN_DIR=$CLIENT_HOME/goldfin/client
OPT=$1

# Check option. 
if [ "$OPT" = "clean" ]; then
  # Wipe out existing generated code. 
  rm -r $CLIENT_CODEGEN_DIR/api
  exit 0
elif [ "$OPT" = "help" ]; then
  shift
  args="help $@"
elif [ "$OPT" = "config-help" ]; then
  shift
  args="config-help $@"
elif [ "$OPT" = "langs" ]; then
  shift
  args="langs $@"
elif [ "$OPT" = "generate" ]; then
  shift
  mkdir -p $CLIENT_CODEGEN_DIR
  args="generate -t $CODEGEN/modules/swagger-codegen/src/main/resources/python -i $CLIENT_HOME/../../swagger/goldfin-service-admin-api.yaml -l python-flask -o $CLIENT_CODEGEN_DIR -DpackageName=api -Dservice $@"
else
  echo "Usage: ${SCRIPT} { generate | clean | help } [ options ]"
  exit 1
fi

# Generate and help require an executable. 
executable="$CODEGEN/modules/swagger-codegen-cli/target/swagger-codegen-cli.jar"
if [ ! -f "$executable" ]
then
  (cd $CODEGEN; mvn clean package)
fi

# Set standard values and execute. 
export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -DloggerPath=conf/log4j.properties"
java $JAVA_OPTS -jar $executable $args
touch $CLIENT_CODEGEN_DIR/api/models/__init__.py
