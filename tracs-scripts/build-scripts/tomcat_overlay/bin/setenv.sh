#!/bin/bash

STANDARD_OPTS="-XX:+UseConcMarkSweepGC -XX:+UseParNewGC \
-Dsun.net.client.defaultConnectTimeout=5000 -Dsun.net.client.defaultReadTimeout=20000 \
-Dsun.lang.ClassLoader.allowArraySyntax=true -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dcom.sun.management.jmxremote \
-server -Djava.awt.headless=true \
-Dsakai.security=/etc/sakai \
$GARBAGECOLLECTOR_OPTS $TIMEOUT_OPTS $COMPATABILITY_OPTS $JVM_OPTS $RUN_OPTS"

SMALL_MEMORY_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=512m -XX:NewSize=256m"
SMALL2_MEMORY_OPTS="-Xmx2304m -XX:MaxMetaspaceSize=512m -XX:NewSize=512m"
MEDIUM_MEMORY_OPTS="-Xmx3072m -XX:MaxMetaspaceSize=512m -XX:NewSize=512m"
LARGE_MEMORY_OPTS="-Xmx10240m -XX:MaxMetaspaceSize=768m -XX:NewSize=1024m"

DEBUGGER_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

DEMO_OPTS="-Dsakai.demo=true"

export CATALINA_PID="$CATALINA_HOME/catalina.pid"

set_properties () {
	rm -f "$CATALINA_HOME/sakai/local.properties"
	ln -s $1 "$CATALINA_HOME/sakai/local.properties"
}

case `hostname` in

	tracs-app-jck-1.its.txstate.edu|\
	tracs-app-mcs-1.its.txstate.edu)
		export CATALINA_OPTS="$STANDARD_OPTS $LARGE_MEMORY_OPTS"
		set_properties local.production.properties
		;;

	tracsappqa1.its.qual.txstate.edu|\
	tracsappqa2.its.qual.txstate.edu)
		export CATALINA_OPTS="$STANDARD_OPTS $MEDIUM_MEMORY_OPTS"
		set_properties local.staging.properties
		;;

	tracscidev1.its.dev.txstate.edu)
		export CATALINA_OPTS="$STANDARD_OPTS $SMALL2_MEMORY_OPTS $DEBUGGER_OPTS"
		set_properties local.dev.properties
		;;

	NickTxState.its.txstate.edu|\
	amysintelmac.its.txstate.edu|\
	amysintelmac.rrhec.txstate.edu)
		export CATALINA_OPTS="$STANDARD_OPTS $SMALL2_MEMORY_OPTS $DEBUGGER_OPTS"
		set_properties local.dev.properties
		;;

	yq12.its.txstate.edu|\
	qyh|\
	yuanhua*)
		#export CATALINA_OPTS="$STANDARD_OPTS $SMALL_MEMORY_OPTS $DEBUGGER_OPTS   $DEMO_OPTS"
		export CATALINA_OPTS="$STANDARD_OPTS $SMALL_MEMORY_OPTS    $DEBUGGER_OPTS"
		set_properties local.yq12.properties
		;;

	vanderbilt*)
		#export CATALINA_OPTS="$STANDARD_OPTS $SMALL_MEMORY_OPTS $DEBUGGER_OPTS   $DEMO_OPTS"
		export CATALINA_OPTS="$STANDARD_OPTS $SMALL_MEMORY_OPTS    $DEBUGGER_OPTS"
		set_properties local.anne.properties
		;;

	*)
		echo "Hostname '"`hostname`"' not recognized."
		exit 1;
		;;

esac

export TZ="US/Central"

(
	JAVA=$(which $JAVA_HOME/bin/java java|head -n1)
	JAVA_VERSION=$(perl -e '($jV) = `'$JAVA' -version 2>&1`=~m/^java version "([\d._]+)"/m; print join("",map({sprintf("%03d",$_)} split(m/[._]/,$jV)));')
	if [[ $JAVA_VERSION < 001007000076 || $JAVA_VERSION > 001007000999 ]]; then
		echo "Java 1.7.0, update 76 or better is required. (java=$JAVA version=$JAVA_VERSION)"
		exit 1
	fi
)

echo $CATALINA_OPTS
ls -l "$CATALINA_HOME/sakai/local.properties"
