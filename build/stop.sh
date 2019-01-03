APPDIR=`pwd`
PIDFILE=$APPDIR/es_data_export.pid
if [ ! -f "$PIDFILE" ] || ! kill -0 "$(cat "$PIDFILE")"; then
echo "es_data_export not running..."
else
echo "stopping es_data_export..."
PID="$(cat "$PIDFILE")"
kill -9 $PID
rm "$PIDFILE"
echo "...es_data_export stopped"
fi


