#!/bin/sh

cd `dirname $0`

jre/bin/java -Xmx${conf.mem.xmx} -Xms${conf.mem.xms} \
     -DentityExpansionLimit=100000000 \
     -Dlogback.configurationFile=conf/logback.xml \
     -Dfile.encoding=UTF-8 \
     ${conf.extra.args} \
     -classpath bundles/guava.jar:bundles/logback-classic.jar:bundles/logback-core.jar:bundles/slf4j-api.jar:bin/org.apache.felix.main.jar:bin/protege-launcher.jar \
     $CMD_OPTIONS org.protege.osgi.framework.Launcher $1



