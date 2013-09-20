#!/bin/sh

java \
   -classpath Contents/Resources/Java/felix.jar:Contents/Resources/Java/ProtegeLauncher.jar \
   -Dorg.protege.launch.config=Contents/Resources/config.xml \
   -Dfile.encoding=UTF-8 -Dlog4j.configuration=file:Contents/Resources/log4j.xml \
   -Dcom.apple.mrj.application.growbox.intrudes=true \
   -DentityExpansionLimit=1000000 \
   -Xmx5G -Xms1G \
   org.protege.osgi.framework.Launcher
