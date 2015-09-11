
java -Dlog4j.configuration=${conf.log4j.location} -DentityExpansionLimit=100000000 -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -classpath bin/guava.jar;bin/org.apache.felix.main.jar;bin/protege-launcher.jar org.protege.osgi.framework.Launcher %1
