#!/bin/bash

### Create Package ###
/Volumes/Data\ Storage/WORKSPACE/mimsWake/mvnw clean package

#cd target
java -jar target/websocket.war --spring.config.location=/config/application.yml --spring.profiles.active=production
