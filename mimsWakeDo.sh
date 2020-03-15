#!/bin/bash
#
PID=/tmp/mimsWakeApp.pid
LOG=/tmp/mimsWakeApp.log

CMD='websocket.war'
COMMAND='java -jar target/websocket.war '

status() {
	echo
	echo "status WAKE for MIMS!!!"

	if [ -f $PID ]
	then
		echo
		echo "Pid file: $( cat $PID ) [$PID]"
		echo
		ps -ef | grep -v grep | grep $( cat $PID )
	else
		echo
		echo "No Pid file"
	fi
}

start() {
	if [ -f $PID ]
	then
		echo
		echo "Already started. PID: [$( cat $PID )]"
	else
		echo -n "starting WAKE for MIMS!!!"
		touch $PID
		if nohup $COMMAND >>$LOG 2>&1 &
		then
			echo $! >$PID
			echo "$(date '+%Y-%m-%d %X'): START" >>$LOG
			tail -f $LOG
		else
			echo "Error.... "
			/bin/rm $PID
		fi
	fi
}

kill_cmd() {
	SIGNAL=""; MSG="Killing "
	while true
	do
		LIST=`ps -ef | grep -v grep | grep $CMD | awk '{print $2}'`
		if [ "$LIST" ]
		then
			echo; echo "$MSG $LIST" ; echo
			echo $LIST | xargs kill $SIGNAL

			sleep 2

			SIGNAL="-9" ; MSG="Killing $SIGNAL"
			if [ -f $PID ]
			then
				/bin/rm $PID
			fi
		else
			echo; echo "All killed..." ; echo
			break
		fi
	done

}

stop() {

	echo "stop WAKE for MIMS!!!"

	if [ -f $PID ]
	then
		if kill $( cat $PID )
		then
			echo "$(date '+%Y-%m-%d %X'): STOP" >>$LOG
		fi
		/bin/rm $PID
		kill_cmd
	else
		echo "No pid file. Already stopped?"
	fi
}


case "$1" in
    'start')
            start
            ;;
    'stop')
            stop
            ;;
    'restart')
            stop ; echo "Sleeping..."; sleep 1 ;
            start
            ;;
    'status')
            status
            ;;
    *)
            echo
            echo "Usage: $0 { start | stop | restart | status }"
            echo
            exit 1
            ;;
esac

exit 0
