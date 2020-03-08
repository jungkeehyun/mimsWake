#!/bin/bash

java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=AFMI0270.kmtf
#java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=KNMI0080.kmtf
#java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=KNMI0081.kmtf
#java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=A2R
#java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=S2R
#java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=S2E
#java -jar sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type=ALL
