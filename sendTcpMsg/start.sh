#!/bin/bash

### Create Package ###
#/Volumes/Data\ Storage/WORKSPACE/sendWake/mvnw clean package

#cd target
java -jar target/sendWake-0.0.1-SNAPSHOT.jar --sendWake.Ip=127.0.0.1 --sendWake.Port=13100 --sendWake.Sid=A2R
java -jar target/sendWake-0.0.1-SNAPSHOT.jar --sendWake.Ip=127.0.0.1 --sendWake.Port=13100 --sendWake.Sid=S2R
java -jar target/sendWake-0.0.1-SNAPSHOT.jar --sendWake.Ip=127.0.0.1 --sendWake.Port=13100 --sendWake.Sid=S2E
