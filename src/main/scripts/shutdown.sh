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

stop() {
  stop_monitor
  stop_app
}

stop
