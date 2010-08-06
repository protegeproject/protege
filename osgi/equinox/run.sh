#!/bin/sh

cd `dirname $0`

java ${CMD_OPTIONS} \
     -Xms500M -Xmx500M \
     -DentityExpansionLimit=100000000 \
     -Dlog4j.configuration=file:log4j.xml \
     -classpath org.eclipse.osgi.jar:ProtegeLauncher.jar \
     org.protege.osgi.framework.Launcher
