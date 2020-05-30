# mimsWake-push-server

## 개요
비즈니스 어플리케이션에서 발생하는 이벤트에 대해 클라이언트에게 메시지를 단방향 Push 해준다.

## 개발환경
A spring boot application + websocket/tcp (by netty)

## 주요 특징
* Netty 프레임워크 기반으로 비동기 통신 사용
* 독립적인 프로세스로 구동되어 비지니스 어플리케이션과 연동
* 클라이언트와의 통신은 TCP Socket, WebSocket 지원
* 송수신 메시지는 JSON 문자열 사용
* 전송 대상 라우팅은 서비스ID, 그룹ID(Mode), 클라이언트ID(MID) 체계를 통해 전체, 특정 그룹, 특정 클라이언트 전송 가능
* 서비스별 큐, 클라이언트별 큐를 사용하여 클라이언트 성능 차이로 인한 간섭과 메시지 유실 방지

# 컴포넌트 설명
## 1. Business Application
* Push 메시지 생성 주체
* Inbound Server에 TCP Socket으로 연결하여 Push 메시지 송신
* 수신된 KMTF 메시지를 JSON 문자열로 변환 사용

## 2. Inbound Server
* 비즈니스 어플리케이션으로부터 Push할 메시지를 수신받는 서버
* 전역적으로 1개 인스턴스만 존재
* TCP Socket 통신방식 사용
* 수신 메시지에 담긴 서비스ID에 해당하는 Inbound Queue에 메시지 추가

### 2-1. Inbound Queue
* 서비스ID에 따라 하나씩 생성되는 메시지 큐
* Inbound Server를 통해 들어온 Push 메시지가 서비스ID를 기준으로 라우팅되어 보관
* 별도 쓰레드를 통해 큐에 담긴 메시지를 서비스ID, 그룹ID(Mode), 클라이언트ID(MID)에 따라 적절한 Outbound Queue로 이동

## 3. Outbound Server
* 클라이언트의 연결을 처리하는 서버
* 설정을 통해 TCP Socket(타 서버)과 WebSocket(클라이언트) 중 선택 가능
  - WebSocket 방식인 경우 Web Socket URI 지정 필요
* 클라이언트 연결/해제시 Outbound Queue 인스턴스 생성/제거
* 클라이언트가 그룹ID(MODE), 클라이언트ID(MID)를 송신하면 이를 해당 채널의 속성으로 설정

### 3-1. Outbound Queue
* 클라이언트가 Outbound Server에 연결시 생성되는 큐
* Inbound Queue가 Push 메시지를 전달하는 대상
* 별도 쓰레드를 통해 큐에 담긴 메시지를 클라이언트 채널에 전송

## 4. Client
* 최종적으로 메시지를 Push받는 대상
* WebBrowser에서 "client/TestWebSocketClient.html" 실행

## 5. 메시지 발생기
* Inbound와 TCP 연결하여 KMTF 메시지를 발송
* 기본설정 파일 : application-sw.properties  (ip, port 설정)
* {TYPE} = {파일명}                          // 동일 폴더의 (KMTF)파일을 읽어서 발송
* {TYPE} = "A2R" | "S2R" | "S2E" | "ALL"   // 임의의 KMTF 생성 및 발송
* start.sh 파일 참조

$java -jar sendTcpMsg/sendWake_0.4.jar --spring.config.location=file:application-sw.properties --sendWake.type={TYPE}

# port that this app use
* InBound  TCP  : 13100
* InBound  File : ~/SharedStorage
* OutBound WebSocket : 13101
* OutBound TCP  : 13102
* OutBound File : ~/SharedStorage

# SSL - netty에서 KEY 파일만 pkcs8 포맷 변환
* openssl pkcs8 -topk8 -inform PEM -outform PEM -in service.key -out service.pkcs8.key -nocrypt

# Building
* ./build.sh
* -- /Volumes/Data\ Storage/WORKSPACE/mimsWake/mvnw clean package
* -- cp target/bin/websocket.war pkg/websocket.war

# Packaging
* pkg
* ./bin
* ./bin/websocket.war [실행파일]
* ./config
* ./config/default.properties [서비스 설정파일]
* ./config/log4j2.xml [로그 설정파일]
* ./logs
* ./logs/mimsWakeApp.log [로그파일]
* ./mimsWakeDo.sh [Start/Stop Script]
* ./SharedStorage [File Read/Write]
* ./tmp [기타]
*
* Usage: ./mimsWakeDo.sh { start | stop | restart | status }
