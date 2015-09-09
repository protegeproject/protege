
jre\bin\java -Dlog4j.configuration=file:log4j.xml -DentityExpansionLimit=100000000 -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -classpath bin/org.apache.felix.main.jar;bin/protege-launcher.jar org.protege.osgi.framework.Launcher %1
