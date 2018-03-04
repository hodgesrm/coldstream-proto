#!/bin/sh
# Generate python code for Swagger model.
# (Source for this script: $CODEGEN/bin/python-petstore.sh)

CODEGEN=$HOME/git/swagger-codegen
SCRIPT="$0"
SCRIPT_DIR=`dirname $SCRIPT`
UI_HOME=`cd $SCRIPT_DIR/..;pwd`
UI_CODEGEN_DIR=$UI_HOME/goldfin-ui-app/src/app/client
OPT=$1

# Check option. 
if [ "$OPT" = "clean" ]; then
  # Wipe out existing generated code. 
  rm -r $UI_CODEGEN_DIR
  exit 0
elif [ "$OPT" = "generate" ]; then
  shift
  mkdir -p $UI_CODEGEN_DIR
  #args="generate -t $CODEGEN/modules/swagger-codegen/src/main/resources/python -i $UI_HOME/../../swagger/goldfin-service-admin-api.yaml -l typescript-angular2 -o $UI_CODEGEN_DIR -DpackageName=api -Dservice $@"
  args="generate -i $UI_HOME/../../swagger/goldfin-service-admin-api.yaml -l typescript-angular2 -o $UI_CODEGEN_DIR -DpackageName=api -Dservice $@"
else
  echo "If you meant to generate code..."
  echo "Usage ${SCRIPT} { generate | clean | <code-gen command> } [ options ]"
  args="$@"
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
