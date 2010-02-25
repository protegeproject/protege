#!/bin/sh

cd `dirname $0`

java -Dlog4j.configuration=file:log4j.xml \
     -Dfelix.config.properties=file:conf/config.properties \
     -Dorg.protege.plugin.dir=plugins \
     -DentityExpansionLimit=100000000 \
     -Dfile.encoding=utf-8 \
     -Dapple.laf.useScreenMenuBar=true  \
     -Dcom.apple.mrj.application.growbox.intrudes=true \
     -classpath felix.jar \
     org.apache.felix.main.Main
