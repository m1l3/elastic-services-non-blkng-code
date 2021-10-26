# Build&Start

Servlet - Tomcat  
**mvn clean install -P servlet spring-boot:run -Dspring-boot.run.profiles=servlet**

WebFlux  
**mvn clean install -P webflux spring-boot:run -Dspring-boot.run.profiles=webflux**

# Testing

#### Iterations: 100,200,400,800 users

#### Delay: 10ms,100ms,500ms

### http-client

GET http://localhost:8082/httpclient/sync?delay=10

GET http://localhost:8082/httpclient/completable-future?delay=10

GET http://localhost:8082/httpclient/webflux?delay=10

### apache-client

GET http://localhost:8082/apacheclient/sync?delay=10

GET http://localhost:8082/apacheclient/completable-future?delay=10

GET http://localhost:8082/apacheclient/webflux?delay=10

### webclient

GET http://localhost:8082/webclient/webflux?delay=10