# push-server-mimsWake

## 개요
비즈니스 어플리케이션에서 발생하는 이벤트에 대해 클라이언트에게 메시지를 단방향 Push 해준다.

## 개발환경
A spring boot application + websocket chat (by netty)

# running in console (콘솔에서 실행)
$mvn clean package

$cd target

$java -jar websocket.war



# running in eclipse (eclipse안에 실행)
execute SpringBootWebApplication class

# open in your browser  
http://localhost:10101/

# port that this app use
10101, 10103

You should open these two ports if you run in another server. 
or you can modify these ports in application.properties.
