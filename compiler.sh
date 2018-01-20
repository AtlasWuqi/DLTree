#!/bin/bash
CPU=wuqi@222.30.48.146
cd src
javac -Djava.ext.dirs=../lib/  edu/xtu/bio/parallel/*.java -d ../bin/
cd ..
echo 'compile finished'
rm -rf bin.tar.gz
tar zcvf bin.tar.gz bin
scp bin.tar.gz $CPU:~/rep/xtu/dltree-standalone/
