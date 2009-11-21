#!/bin/sh

#DEBUG_OPT="-Xdebug -Xrunjdwp:transport=dt_socket,address=8500,server=y,suspend=y"
java -Dlog4j.configuration=file:log4j.xml ${DEBUG_OPT} \
     -Dorg.protege.plugin.dir=jars/plugins \
     -classpath framework.jar:crimson.jar \
     org.knopflerfish.framework.Main
