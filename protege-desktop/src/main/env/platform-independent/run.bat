
java ${conf.extra.args} -DentityExpansionLimit=100000000 -Dlogback.configurationFile=conf/logback.xml -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -classpath bin/guava.jar;bin/org.apache.felix.main.jar;bin/protege-launcher.jar org.protege.osgi.framework.Launcher %1
