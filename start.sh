#!/bin/bash

#java -jar target/websocket.war
java -Dlog4j.configurationFile=./log4j2.xml -jar target/websocket.war 
