#!/bin/sh

cd `dirname $0`

java -Xmx500M -Xms250M \
     -Dlog4j.configuration=file:log4j.xml \
     -DentityExpansionLimit=100000000 \
     -Dfile.encoding=UTF-8 \
     -classpath bin/org.apache.felix.main.jar:bin/ProtegeLauncher.jar \
     $CMD_OPTIONS org.protege.osgi.framework.Launcher $1



