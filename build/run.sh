APPDIR=`pwd`
PIDFILE=$APPDIR/es_data_export.pid
if [ -f "$PIDFILE" ] && kill -0 $(cat "$PIDFILE"); then
echo "es_data_export is already running..."
exit 1
fi
nohup java -jar $APPDIR/es_data_export.jar >/dev/null 2>&1 &
echo $! > $PIDFILE
echo "start es_data_export..."


