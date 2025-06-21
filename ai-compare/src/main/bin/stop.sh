#!/usr/bin/env bash
APP_NAME="AiCompareMain"

current_path=`pwd`

bin=`dirname "$0"`
bin=`cd "$bin/.."; pwd`

echo $bin

get_pid() {
  JAVA_PID=`ps aux |grep "${APP_NAME}" |grep -v grep|awk '{print $2}'`
  echo $JAVA_PID;
}

pidfile=$bin/bin/pid.pid

pid=`get_pid`

if [ "$pid" == "" ] ; then
	echo "${APP_NAME} is not running. exists"
	exit 0
fi

echo -e "`hostname`: stopping ${APP_NAME} $pid ... "
kill $pid

if [ -f "$pidfile" ];then
 `rm $pidfile`
fi

LOOPS=0
while (true);
do
	gpid=`get_pid`
    if [ "$gpid" == "" ] ; then
    	echo "~~ok! cost:$LOOPS"
    	break;
    fi
    let LOOPS=LOOPS+1
    sleep 1
done
cd $current_path