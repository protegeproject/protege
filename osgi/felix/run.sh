#!/bin/sh

cd `dirname $0`

java -Xmx500M -Xms250M \
     -Dlog4j.configuration=file:log4j.xml \
     -DentityExpansionLimit=100000000 \
     -classpath bin/felix.jar:bin/ProtegeLauncher.jar \
     org.protege.osgi.framework.Launcher
