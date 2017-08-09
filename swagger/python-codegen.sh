#!/bin/sh
# Generate python code for Swagger model.


CODEGEN=$HOME/git/swagger-codegen

#SCRIPT="$0"
#while [ -h "$SCRIPT" ] ; do
#  ls=`ls -ld "$SCRIPT"`
#  link=`expr "$ls" : '.*-> \(.*\)$'`
#  if expr "$link" : '/.*' > /dev/null; then
#    SCRIPT="$link"
#  else
#    SCRIPT=`dirname "$SCRIPT"`/"$link"
#  fi
#done

#if [ ! -d "${APP_DIR}" ]; then
#  APP_DIR=`dirname "$SCRIPT"`/..
#  APP_DIR=`cd "${APP_DIR}"; pwd`
#fi

executable="$CODEGEN/modules/swagger-codegen-cli/target/swagger-codegen-cli.jar"

if [ ! -f "$executable" ]
then
  (cd $CODEGEN; mvn clean package)
fi

# if you've executed sbt assembly previously it will use that instead.
export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -DloggerPath=conf/log4j.properties"
ags="generate -t $CODEGEN/modules/swagger-codegen/src/main/resources/python -i coldstream-proto.yaml -l python -o ../generated/python -DpackageName=coldstream_api $@"

java $JAVA_OPTS -jar $executable $ags
