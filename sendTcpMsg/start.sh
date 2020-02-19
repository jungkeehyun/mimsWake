#!/bin/bash

### Create Package ###
#/Volumes/Data\ Storage/WORKSPACE/sendWake/mvnw clean package

#cd target
#java -jar target/sendWake-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
java -jar sendWake-0.0.1-SNAPSHOT.jar --sendWake.Ip=127.0.0.1 --sendWake.Port=8000
