#!/bin/bash

CLASSPATH="$CLASSPATH":"./bin"
for i in ./lib/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done
java -cp $CLASSPATH edu.xtu.bio.utils.MD5Util $1

