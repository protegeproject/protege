setlocal
cd /d %~dp0
jre\bin\java ${conf.extra.args} -DentityExpansionLimit=100000000 -Dlogback.configurationFile=conf/logback-win.xml -Dfile.encoding=utf-8 -Dorg.protege.plugin.dir=plugins -classpath bundles/guava.jar;bundles/logback-classic.jar;bundles/logback-core.jar;bundles/slf4j-api.jar;bundles/glassfish-corba-orb.jar;bundles/org.apache.felix.main.jar;bundles/maven-artifact.jar;bundles/protege-launcher.jar org.protege.osgi.framework.Launcher %1
