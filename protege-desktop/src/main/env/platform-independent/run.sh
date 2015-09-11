#!/bin/sh

cd `dirname $0`

java -Xmx${conf.mem.xmx} -Xms${conf.mem.xms} \
     -Dlog4j.configuration=${conf.log4j.location} \
     -DentityExpansionLimit=100000000 \
     -Dfile.encoding=UTF-8 \
     -classpath bin/guava.jar:bin/org.apache.felix.main.jar:bin/protege-launcher.jar \
     $CMD_OPTIONS org.protege.osgi.framework.Launcher $1



