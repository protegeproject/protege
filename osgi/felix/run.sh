#!/bin/sh

cd `dirname $0`

java -Xmx500M -Xms250M \
     -Dlog4j.configuration=file:log4j.xml \
     -Dorg.protege.plugin.dir=plugins \
     -DentityExpansionLimit=100000000 \
     -Dfile.encoding=utf-8 \
     -Dapple.laf.useScreenMenuBar=true  \
     -Dcom.apple.mrj.application.growbox.intrudes=true \
     -classpath bin/felix.jar \
     org.apache.felix.main.Main
