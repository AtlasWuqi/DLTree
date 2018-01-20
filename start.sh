#!/bin/bash

rm -rf logs/*
rm -rf main.log

CLASSPATH="$CLASSPATH":"./bin"
for i in ./lib/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done
#echo $CLASSPATH
java -cp $CLASSPATH -Xmx25g -Xms25g -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:-PrintGC  \
edu.xtu.bio.parallel.InnerDBBuilder >> main.log 2>&1 &

