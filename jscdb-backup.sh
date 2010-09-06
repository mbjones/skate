#!/bin/bash

#
# Backup the JSCDB database to a text file and copy it offsite
#
# Install in crontab with this:
#23 0,3,6,9,12,15,18,21 * * *       $HOME/jscdb-backup/jscdb-backup.sh >> $HOME/jscdb-backup/jscdb-backup.log 2>&1
# Matt Jones 6 Sep 2009

DIR=/home/jones/jscdb-backup
DBNAME=jscdb
HOST=localhost
USER=jscdb
REMOTEUSER=juneausk
REMOTEHOST=juneauskatingclub.org
REMOTEPORT=7822
REMOTEDIR=backup/jscdb
DISKDIR=/Volumes/icestore/jscdb-backup

DATESTAMP=`date "+%Y%m%d-%H%M%S"`
HOUR=`date "+%H"`
DAY=`date "+%w"`
DOM=`date "+%d"`
MIDNIGHT=0
SUNDAY=0
PGDUMP=/usr/bin/pg_dump
TOMCAT=/etc/tomcat6
NAME=$DATESTAMP-$DBNAME-backup
BACKUPDIR=$DIR/$NAME
FILENAME="-$DBNAME-backup.sql"
DUMPFILE=$BACKUPDIR/$NAME.sql
LONGTERM=$DIR/longterm
echo "Dump file: $DUMPFILE"

# Create the temporary backup directory
mkdir $BACKUPDIR

# Create the database backup file
#$PGDUMP -f $DUMPFILE -U $USER -h $HOST $DBNAME
$PGDUMP -f $DUMPFILE -U $USER $DBNAME

# Backup the config files and app jar as well once a week
if test $HOUR -eq $MIDNIGHT && test $DAY -eq $SUNDAY; then
    echo "It is $HOUR on $SUNDAY so backing up app files."
    cp /etc/apache2/sites-available/jsc-reg $BACKUPDIR
    cp /etc/apache2/sites-available/jsc-apps $BACKUPDIR
    cp -r /var/jscdb $BACKUPDIR
    #cp -R $TOMCAT/Catalina/apps.juneauskatingclub.org/ROOT.xml $BACKUPDIR
    cp -R $TOMCAT/Catalina/reg.juneauskatingclub.org/ROOT.xml $BACKUPDIR
fi

# tar and gzip the backup directory
pushd $DIR
tar czf $NAME.tgz $NAME
popd

# Copy it to a remote host
scp -P $REMOTEPORT $BACKUPDIR.tgz $REMOTEUSER@$REMOTEHOST:$REMOTEDIR

# Copy it to a local external disk
#cp -p $BACKUPDIR.tgz $DISKDIR

# Remove the temporary directory
rm -rf $BACKUPDIR

# Move first sunday of month backup to longterm directory
if test $HOUR -eq $MIDNIGHT && test $DAY -eq $SUNDAY && test $DOM -lt 8; then
    echo "Preserving longterm backup."
    if test ! -d $LONGTERM; then
        echo "Making longterm dir $LONGTERM"
        mkdir $LONGTERM
    fi
    mv $BACKUPDIR.tgz $LONGTERM
fi

# Prune any older backups that are more than 90 days old
find . \( ! -name . -prune \) -mtime +90 -name \*.tgz -exec rm {} \;
