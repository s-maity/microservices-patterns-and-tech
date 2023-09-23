package org.example.users;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

@Service
@Slf4j
public class UserDetailsAggregateService {

    private final String userDetailsMicroservice = "http://user-details-microservice/api/v1/users/";
    private final String userCareerMicroservice = "http://user-career-microservice/api/v1/users/";
    private final String userStatus = "http://user-career-microservice/api/v1/users/";

    @Autowired
    private RestTemplate restTemplate;

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

    @Retry(name = "userCareer", fallbackMethod = "defaultUserCareer")
    public UserCareerDto getUserCareerUsingRestTemplate(int id) {
        System.out.println("Call/Retry user career at:" + LocalDateTime.now());
        ResponseEntity<UserCareerDto> response
                = restTemplate.getForEntity(userCareerMicroservice + id, UserCareerDto.class);
        var userCareer = response.getBody();
        log.info("userCareer={}", userCareer);
        return userCareer;
    }

    public UserCareerDto defaultUserCareer(Exception e) {
        return UserCareerDto.builder()
                .id(0)
                .skills(Arrays.asList("Unknown"))
                .build();
    }


    @RateLimiter(name = "userStatus")
    public String getUserStatus(int id) {
        ResponseEntity<String> response
                = restTemplate.getForEntity(userDetailsMicroservice + "status/" + id, String.class);
        return response.getBody();
    }

}
