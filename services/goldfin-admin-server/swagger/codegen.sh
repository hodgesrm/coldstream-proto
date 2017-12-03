#!/bin/sh
# Generate Java code from global API model. 
# (Source for this script: $CODEGEN/bin/python-petstore.sh)

SCRIPT="$0"
SCRIPT_DIR=`dirname $SCRIPT`
API_HOME=`cd $SCRIPT_DIR/..;pwd`
API_CODEGEN_DIR=$API_HOME/src/gen/java
CODEGEN_HOME=$HOME/git/swagger-codegen
SWAGGER_HOME=$API_HOME/../../swagger
OPT=$1

# Check option. 
if [ "$OPT" = "clean" ]; then
  # Wipe out existing generated code. 
  rm -r $API_CODEGEN_DIR
  exit 0
elif [ "$OPT" = "help" ]; then
  shift
  args="help $@"
elif [ "$OPT" = "config-help" ]; then
  shift
  args="config-help $@"
elif [ "$OPT" = "generate" ]; then
  shift
  args="$@ generate -t $CODEGEN_HOME/modules/swagger-codegen/src/main/resources/JavaJaxRS -i $SWAGGER_HOME/coldstream-proto.yaml -l jaxrs -o $API_HOME -DhideGenerationTimestamp=true,serverPort=8080 --model-package=io.goldfin.front.invoice.api.model --api-package=io.goldfin.front.invoice.api.service --invoker-package=io.goldfin.front.invoice.api.invoker"
else
  echo "Usage: ${SCRIPT} { generate | clean | help } [ options ]"
  exit 1
fi

# Generate and help require an executable. 
set -x
executable="$CODEGEN_HOME/modules/swagger-codegen-cli/target/swagger-codegen-cli.jar"
if [ ! -f "$executable" ]
then
  (cd $CODEGEN; mvn clean package)
fi

# Set standard values and execute. 
export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -DloggerPath=conf/log4j.properties"
java $JAVA_OPTS -jar $executable $args 
