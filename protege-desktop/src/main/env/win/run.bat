setlocal
cd /d %~dp0
jre\bin\java -Xmx${conf.mem.xmx} -Xms${conf.mem.xms} ${conf.extra.args} -DentityExpansionLimit=100000000 -Dlogback.configurationFile=conf/logback.xml -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -classpath bundles/guava.jar;bundles/jansi.jar;bundles/logback-classic.jar;bundles/logback-core.jar;bundles/slf4j-api.jar;bin/org.apache.felix.main.jar;bin/protege-launcher.jar org.protege.osgi.framework.Launcher %1
