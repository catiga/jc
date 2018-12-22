#!/bin/sh
# chkconfig settings
# jcserver settings

os_shell="linux_centos_65-x86-64"
os_kernel="2.6.32-696.6.3.el6.x86_64"
JC_HOME=$(cd `dirname $0`; pwd)
os_apm_uking="jcserver"
JC_OS_FP="$JC_HOME/$os_apm_uking"
os_apm_uver="1.13.0.1_dep"
sys_log="/home/jclogs"

RUNNING_USER=root

CLASSPATH=$JC_HOME/classes
for i in "$JC_HOME"/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done

APP_MAINCLASS=com.jeancoder.root.server.fk.Starter

JAVA_OPTS="-ms1024m -mx1024m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m"

psid=0
checkpid() {
   echo "Checking $JC_OS_FP"
   javaps=`ps -ef | grep $JC_OS_FP | grep -v "grep"`

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
		cd /home
        BASE_DIR=$(pwd)
        LIB="${BASE_DIR}/jcshell/"
        echo -n "Choose libs $LIB"
        JAVA_CMD="nohup java $JAVA_OPTS -classpath $CLASSPATH $APP_MAINCLASS >/dev/null 2>&1 &"
        
        su - $RUNNING_USER -c "$JAVA_CMD"
        
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

