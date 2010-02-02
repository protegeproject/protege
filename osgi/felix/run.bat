
java -Dlog4j.configuration=file:log4j.xml -DentityExpansionLimit=100000000 -Dfile.encoding=utf-8 -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl  -Dorg.protege.plugin.dir=plugins -classpath bin/felix.jar;bin/crimson.jar org.apache.felix.main.Main 
