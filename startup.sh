#!/bin/sh
# chkconfig settings
# jcserver settings

os_shell="linux_centos_65-x86-64"
os_kernel="2.6.32-696.6.3.el6.x86_64"
os_apm_uking="jcserver"
os_apm_uver="1.13.0.1_dep"

JAVA_OPTS=" -Xmx2048m -XX:PermSize=64m -XX:MaxPermSize=512m -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=1 -XX:GCLogFileSize=1024k -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs/mtdperf.hprof -server -Dfile.encoding=UTF-8"

psid=0
checkpid() {
   javaps=`ps -ef | grep $os_apm_uking | grep -v "grep"`

   if [ -n "$javaps" ]; then
      psid=`echo $javaps | awk '{print $2}'`
   else
      psid=0
   fi
}

checktostart() {
	checkpid
	if [ $psid -ne 0 ]; then
		echo "================================"
		echo "warn: $os_apm_uking already started! (pid=$psid)"
		echo "================================"
	else
		echo -n "Starting $os_apm_uking ..."
		cd ~/shell/bin/jc
		BASE_DIR=$(pwd)
		LIB="${BASE_DIR}/lib/"
		nohup java ${JAVA_OPTS} -server -classpath "${LIB}/*:${LIB}/droolsRuntime/*:eiServer.jar" ${START_CLASS} &
		checkpid
		if [ $psid -ne 0 ]; then
			echo "(pid=$psid) [OK]"
		else
			echo "[Failed]"
		fi
	fi
}

checktostop() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo -n "Stopping $os_apm_uking ...(pid=$psid) "
      kill -9 $psid
      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[Failed]"
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         stop
      fi
   else
      echo "================================"
      echo "warn: $os_apm_uking is not running"
      echo "================================"
   fi
}

checkstatus() {
   checkpid

   if [ $psid -ne 0 ];  then
      echo "$os_apm_uking is running! (pid=$psid)"
   else
      echo "$os_apm_uking is not running"
   fi
}

case "$1" in
   'start')
      checktostart
     ;;
   'stop')
     checktostop
     ;; 
   'restart')
     checktostop
     checktostart
     ;;
   'status')
     checkstatus
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|status|info}"
     exit 1
esac

