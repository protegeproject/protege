#!/bin/sh

cd `dirname $0`

java ${CMD_OPTIONS} -Xms500M -Xmx500M -Dosgi.clean=true -DentityExpansionLimit=100000000 -Dfile.encoding=utf-8 -jar org.eclipse.osgi.jar
