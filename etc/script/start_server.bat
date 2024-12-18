@ECHO off
title "KBCoupon Server"
SET JAVA_PATH=E:/dev/Java/AmazonCorretto/jdk1.8.0_412/bin
SET START_PATH=E:/2sBreakers/project/kbdream/src/KBCoupon/build/libs

cd %START_PATH%
%JAVA_PATH%\java -jar -server -XX:+UseG1GC -Xmx1G -Dsvr.nm=LOC_KBCP_MAIN11 -Dfile.encoding=UTF-8 KBCoupon.jar
pause
exit
