# Microservices-Design pattern
-----------------------
1. Service discovery using Spring Cloud Eureka Discovery Server and Eureca client.
2. Circuit breaker pattern using Spring Cloud and Resilience4j.
3. API gateway using Spring Cloud Gateway
4. Microservices logging tracing using Micrometer & zipkin
5. Spring cloud config server for dynamic configuration for microservices. Update configuration
   without CI CD
------------------------
## Service discovery using Eureka

To create Eureka discovery server, Create a new spring boot app add the below dependency - 

    <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

In Main application, add this annotation and property
   
    @EnableEurekaServer

    server:port: 8761 -- default for Eureka server

To register clients, with Eureka server, add below dependency and property

    <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    spring.application.name: user-details-microservice

On application start, microservice will register itself with Eureka server running on default port

In my setup, 

    user-microservice 
                        -> user-details-microservice 
                        -> user-career-microservice

Add @EnableDiscoveryClient in user-microservice main class

Since all microservices are registered with Eureka server, user-microservice can call other microservices by name
(registered with Eureka) instead of hardcoded ip. 

    private final String userDetailsMicroservice = "http://user-details-microservice/api/v1/users/";
    private final String userCareerMicroservice = "http://user-career-microservice/api/v1/users/";


## Circuit breaker using Spring cloud resilience4j

Add the below dependencies in user-microservices

    <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    </dependency>
    <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
     </dependency>

Add the configurations for circuit breaking -
 - https://github.com/s-maity/microservices-patterns-and-tech/blob/circuit-breaker/user-microservice-async/src/main/resources/application.yaml

Code for circuit breaker and fallback:
- https://github.com/s-maity/microservices-patterns-and-tech/blob/circuit-breaker/user-microservice-async/src/main/java/org/example/users/UserDetailsAggregateService.java

      @CircuitBreaker(name = "userDetailsServiceCircuitBreaker", fallbackMethod = "getDefaultUserDetails")
      public UserDetailsDto getUserDetailsUsingRestTemplate(int id) {
      ResponseEntity<UserDetailsDto> response
       = restTemplate.getForEntity(userDetailsMicroservice + id, UserDetailsDto.class);
   
           var userDetails = response.getBody();
           log.info("user={}", userDetails);
           return userDetails;
       }
   
       public UserDetailsDto getDefaultUserDetails(Exception e) {
           return UserDetailsDto.builder()
                   .id(0)
                   .name("Unknown")
                   .org("Unknown")
                   .build();
       }

We can check the status of the circuit in this end point -
   http://localhost:8073/actuator/health

We can also implement retry, ratelimiter etc. 

![img.png](img.png)  source: https://reflectoring.io/retry-with-resilience4j/


## API gateway

Create a new Spring boot application and add the below dependency

      <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-gateway</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

Configure API gateway routes

    spring:
      cloud:
        gateway:
          routes:
          - id: user_details
            uri: lb://USER-DETAILS-MICROSERVICE
            predicates:
              - Path=/api/v1/users/{id}

          - id: user_career_details
            uri: lb://USER-CAREER-MICROSERVICE
            predicates:
              - Path=/api/v1/user-career/{id}


We can configure circuit breaker in api gateway easily 
Ref: https://youtu.be/HY4kTCNKwxk?si=uB2x6V0x2Y9cENvl

  - Add resiliance4J dependency 
    - add yaml config

          spring:
             cloud:
               gateway:
                 routes:
                   - id: user_details
                     uri: lb://USER-DETAILS-MICROSERVICE
                      predicates:
                         - Path=/api/v1/users/{id}
                      filters:
                          - SetPath=/api/v1/users/{id}
                       - name: CircuitBreaker
                          args:
                          name: myCircuitBreaker
                           fallbackUri: forward:/api/v1/users/fallback
						   
						   
## Logging and Tracing
Run zipkin server in local (default port: 9411)

Add the below dependencies and property in all microservices - 

	<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-brave</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.reporter2</groupId>
            <artifactId>zipkin-reporter-brave</artifactId>
        </dependency>
		
		spring.application.name: user-career-microservice
		management.tracing.sampling.probability: 1.0
		
Trace request in Zipkin UI http://localhost:9411/zipkin/

## Config Server

Create a git repo with application.yml, application-dev.yaml etc.

    https://github.com/s-maity/spring-cloud-config-repo

Create new Spring boot project and add the below dependency - 

      <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>

Add the below properties: 

    server.port: 8888 -- config serve default port

    spring:
      config:
          name: configserver
      cloud:
        config:
          server:
            git:
              uri: https://github.com/s-maity/spring-cloud-config-repo

Config Server is ready. Now configure microservice to read configuration form config server


Add the following dependency and properties. Active profile can be managed externally. Based on the 
active profile, the config will be read from the config repo. 

    <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    
    spring:
      config:
        import: optional:configserver:http://localhost:8888
       profiles:
         active: dev
      management:
        endpoints:
          web:
            exposure:
              include: health,info,refresh

    @Service
    @RefreshScope
    public class UserService {
      @Value("${app.microservices.message: Have patience}")
      private String message;
       ....
    }

After You update configuration in git repo, call this end point to dynamically refresh -
  [POST] http://localhost:8072/actuator/refresh