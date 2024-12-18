#!/bin/bash
cd /home/zlgoon/service/stardream/stardream-manager/webapps
KEYWORD="KBCouponAdmin.jar"
find_java_svr_pid() {
  PID=$(ps aux | grep "$KEYWORD" | grep -v grep | awk '{print $2}')
}
kill_java_svr() {
  if [ -n "$PID" ]; then
    kill "$PID"
    echo "Java 프로세스 (PID: $PID)를 종료했습니다."
  else
    echo "실행 중인 Java 프로세스를 찾을 수 없습니다."
  fi
}
find_java_svr_pid
kill_java_svr
nohup java -jar -server -Xmx3g -XX:+UseG1GC -Djava.awt.headless=true -Dspring.profiles.active=prod -Dsvr.nm=PRDD_KBCP_84 -Dfile.encoding=UTF-8 KBCouponAdmin.jar 1> /dev/null 2>&1 &
