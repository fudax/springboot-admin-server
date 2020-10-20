#!/bin/bash
BINFILE=sep-octopus
BIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd $BIN_DIR
MONITOR_LOG="$BIN_DIR/monitor.log"
MONITOR_PIDFILE="$BIN_DIR/monitor.pid"
MONITOR_PID=0
if [[ -f $MONITOR_PIDFILE ]]; then
  MONITOR_PID=`cat $MONITOR_PIDFILE`
fi
PIDFILE="$BIN_DIR/$(basename $BINFILE).pid"
PID=0
if [[ -f $PIDFILE ]]; then
  PID=`cat $PIDFILE`
fi

CONF_DIR=./config
LIB_DIR=./lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
JAVA_OPTS=" -Djava.net.preferIPv4Stack=true -Dfile.encoding=utf-8 -Dspring.profiles.active=dev"
JAVA_MEM_OPTS=" -server -Xms6g -Xmx6g -XX:SurvivorRatio=2 -XX:+UseParallelGC "

#START_CMD=$BIN_DIR/$BINFILE
BINMAIN=com.fudax.sep.octopus.SepOctopusApplication
START_CMD="java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $BINMAIN"
STOP_CMD="kill $PID"
MONITOR_INTERVAL=5

running() {
  if [[ -z $1 || $1 == 0 ]]; then
      echo 0
      return
  fi
  if [[ -d /proc/$1 ]]; then
      echo 1
      return
  fi
  echo 0
  return
}

start_app() {
  echo "### starting $BINFILE `date '+%Y-%m-%d %H:%M:%S'` ###" >>  /dev/null   2>&1 &
  nohup java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $BINMAIN >> /dev/null 2>&1 &
  if [[ $(running $!) == 0 ]]; then
    echo "failed to start $BINFILE"
    exit 1
  fi
  PID=$!
  echo $! > $PIDFILE
  echo "new pid sep-octopus-$!"
}

stop_app() {
  if [[ $(running $PID) == 0 ]]; then
    return
  fi
  echo "stopping $PID of $BINFILE ..."
  $STOP_CMD
  sleep 1
  $STOP_CMD
  sleep 1
  $STOP_CMD
}


start_monitor() {
  while [ 1 ]; do
    if [[ $(running $PID) == 0 ]]; then
      echo "$(date '+%Y-%m-%d %T') $BINFILE is gone" >> $MONITOR_LOG
      start_app
      echo "$(date '+%Y-%m-%d %T') $BINFILE started" >> $MONITOR_LOG
    fi
    sleep $MONITOR_INTERVAL
  done &
  MONITOR_PID=$!
  echo "monitor pid sep-octopus-$!"
  echo $! > $MONITOR_PIDFILE
}


stop_monitor() {
  if [[ $(running $MONITOR_PID) == 0 ]]; then
    return
  fi
  echo "stopping $MONITOR_PID of $BINFILE monitor ..."
  kill $MONITOR_PID
  sleep 1
  kill $MONITOR_PID
  sleep 1
  kill $MONITOR_PID
}

start() {
  start_app
#  start_monitor
}

stop() {
#  stop_monitor
  stop_app
}

restart() {
  stop
  start
}

restart
