
java -server -Dlog4j.configuration=file:log4j.xml -DentityExpansionLimit=100000000 -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -classpath bin/felix.jar;bin/ProtegeLauncher.jar org.protege.osgi.framework.Launcher
