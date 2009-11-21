#!/bin/sh

rm -rf ${HOME}/.felix/Protege
java -Dlog4j.configuration=file:log4j.xml \
     -Dorg.protege.plugin.dir=plugins \
     -classpath bin/felix.jar:bin/crimson.jar \
     org.apache.felix.main.Main
