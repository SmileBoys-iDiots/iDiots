@echo off & color 0E
@java -d64 -server -XX:NewSize=2G -XX:MaxNewSize=4G -Xms8G -Xmx24G -XX:SurvivorRatio=16 -XX:+UseG1GC -cp l1jserver.jar;lib\JTattoo.jar;lib\xmlapi;lib\c3p0-0.9.1.2.jar;lib\netty-all-4.0.29.Final.jar;lib\commons-dbcp-1.4.jar;lib\commons-pool-1.6.jar;lib\mysql-connector-java-5.1.21-bin.jar;lib\javolution.jar;lib\netty-all-4.0.29.Final.jar server.manager.eva
@pause