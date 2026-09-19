@echo off
setlocal
cd /d %~dp0

jre\bin\java ^
  --add-opens java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED ^
  --add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED ^
  --add-opens java.base/java.lang=ALL-UNNAMED ^
  --add-opens java.base/java.net=ALL-UNNAMED ^
  --add-opens java.base/java.util=ALL-UNNAMED ^
  --add-opens java.base/sun.net.www=ALL-UNNAMED ^
  --add-opens java.base/jdk.internal.loader=ALL-UNNAMED ^
  --add-exports java.base/sun.security.action=ALL-UNNAMED ^
  -XX:CompileCommand=exclude,javax/swing/text/GlyphView,getBreakSpot ^
  -DentityExpansionLimit=100000000 ^
  -Dlogback.configurationFile=conf/logback-win.xml ^
  -Dfile.encoding=utf-8 ^
  -Dorg.protege.plugin.dir=plugins ^
  -classpath "lib/org.eclipse.osgi.jar;bundles/guava.jar;bundles/logback-classic.jar;bundles/logback-core.jar;bundles/slf4j-api.jar;bundles/glassfish-corba-orb.jar;bundles/org.apache.felix.main.jar;bundles/maven-artifact.jar;bundles/protege-launcher.jar" ^
  org.protege.osgi.framework.Launcher %1