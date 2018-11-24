#!/bin/sh

cd ~/shell/bin/jc
java -Xms16m \
-Xmx48m \
-XX:MaxMetaspaceSize=64m \
-XX:CompressedClassSpaceSize=8m \
-Xss256k \
-Xmn8m \
-XX:InitialCodeCacheSize=4m \
-XX:ReservedCodeCacheSize=8m \
-XX:MaxDirectMemorySize=16m \
-jar support-registry-0.0.1-SNAPSHOT.jar
