#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
cd "$( cd -P "$( dirname "$SOURCE" )" && pwd )"

conf_file=
if [ -f "$HOME/.protege/conf/jvm.conf" ]; then
  conf_file="$HOME/.protege/conf/jvm.conf"
elif [ -f conf/jvm.conf ]; then
  conf_file=conf/jvm.conf
fi

EXTRA_JVM_OPTIONS=
if [ -n "$conf_file" ]; then
  EXTRA_JVM_OPTIONS=$(sed -n 's/^max_heap_size=/-Xmx/p; s/^min_heap_size=/-Xms/p; s/^stack_size=/-Xss/p; s/^append=//p;' "$conf_file" | tr '\n' ' ')
fi

jre/bin/java \
     -DentityExpansionLimit=100000000 \
     -Dlogback.configurationFile=conf/logback.xml \
     -Dfile.encoding=UTF-8 \
     ${conf.extra.args} \
     -classpath bundles/guava.jar:bundles/logback-classic.jar:bundles/logback-core.jar:bundles/slf4j-api.jar:bundles/glassfish-corba-orb.jar:bundles/org.apache.felix.main.jar:bundles/maven-artifact.jar:bundles/protege-launcher.jar \
     $CMD_OPTIONS $EXTRA_JVM_OPTIONS org.protege.osgi.framework.Launcher $1



