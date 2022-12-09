#!/bin/bash

cd "$(dirname "$0")"

./jre/bin/java \
     -Dlogback.configurationFile=conf/logback.xml \
     -DentityExpansionLimit=100000000 \
     -Dfile.encoding=UTF-8 \
     -XX:CompileCommand=exclude,javax/swing/text/GlyphView,getBreakSpot \
     -Dapple.laf.useScreenMenuBar=true \
     -Dcom.apple.mrj.application.apple.menu.about.name=Protege \
     -Xdock:name=Protege \
     -Xdock:icon=Resources/Protege.icns \
     -classpath bundles/guava.jar:bundles/logback-classic.jar:bundles/logback-core.jar:bundles/slf4j-api.jar:bundles/glassfish-corba-orb.jar:bundles/org.apache.felix.main.jar:bundles/maven-artifact.jar:bundles/protege-launcher.jar \
     org.protege.osgi.framework.Launcher $1
