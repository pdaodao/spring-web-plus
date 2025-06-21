#!/bin/bash
APP_NAME="AiCompareMain"
MAIN_CLASS="com.github.pdaodao.aicompare.AiCompareMain"

JAVA_OPTS="-server -Xms1g -Xmx1g -Djava.io.tmpdir=./tmp"

#JAVA="/deploy/jdk1.8.0_111/bin/java"
JAVA=$(which java)
echo $JAVA

current_path=`pwd`

bin=`dirname "$0"`
bin=`cd "$bin/.."; pwd`

echo $bin

export LANG=en_US.UTF-8

if [ ! -d $bin/logs ] ; then
	mkdir -p $bin/logs
fi

if [ ! -d $bin/tmp ] ; then
	mkdir -p $bin/tmp
fi

JAVA_OPTS="$JAVA_OPTS"
JAVA_OPTS=" $JAVA_OPTS -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"

CLASSPATH="$bin/conf:$bin/jar/*":"$bin/lib/*"

echo "$JAVA $JAVA_OPTS -classpath $CLASSPATH "

echo "-- starting at ${bin}"

cd $bin

nohup $JAVA $JAVA_OPTS -DappName=${APP_NAME} -classpath $CLASSPATH $MAIN_CLASS --spring.config.location=${bin}/conf/ 1>>/dev/null 2>&1 &
echo $! > $bin/bin/pid.pid
sleep 1
echo "-- started"
cat $bin/bin/pid.pid
cd $current_path
