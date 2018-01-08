#!/bin/sh
# Generate python code for Swagger model.
# (Source for this script: $CODEGEN/bin/python-petstore.sh)

CODEGEN=$HOME/git/swagger-codegen
SCRIPT="$0"
SCRIPT_DIR=`dirname $SCRIPT`
CS_HOME=`cd $SCRIPT_DIR/..;pwd`
#CS_CODEGEN_DIR=$CS_HOME/generated
CS_CODEGEN_DIR=$CS_HOME
MODULE_NAME=goldfin_storage
SWAGGER_YAML=goldfin-storage.yaml
OPT=$1

# Check option. 
if [ "$OPT" = "clean" ]; then
  # Wipe out existing generated code. 
  echo "DOING NOTHING"
  exit 0
elif [ "$OPT" = "help" ]; then
  shift
  args="help $@"
elif [ "$OPT" = "config-help" ]; then
  shift
  args="config-help $@"
elif [ "$OPT" = "generate" ]; then
  shift
  mkdir -p $CS_HOME/generated
  args="generate -t $CODEGEN/modules/swagger-codegen/src/main/resources/python -i $CS_HOME/swagger/$SWAGGER_YAML -l python-flask -o $CS_CODEGEN_DIR -DpackageName=$MODULE_NAME -Dservice $@"
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
export JAVA_OPTS="${JAVA_OPTS} -Xmx1024M -DloggerPath=conf/log4j.properties"
java $JAVA_OPTS -jar $executable $args
#touch $CS_CODEGEN_DIR/api/models/__init__.py
