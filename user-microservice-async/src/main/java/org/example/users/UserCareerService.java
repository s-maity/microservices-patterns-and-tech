package org.example.users;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserCareerService {

    @Autowired
    private RestTemplate restTemplate;

    private final String userCareerMicroservice = "http://user-career-microservice/api/v1/users/";

    @Retry(name = "userCareer", fallbackMethod = "defaultUserCareer")
    public UserCareerDto getUserCareerUsingRestTemplate(int id) {
        System.out.println("call user career at:" + LocalDateTime.now());
        ResponseEntity<UserCareerDto> response
                = restTemplate.getForEntity(userCareerMicroservice + id, UserCareerDto.class);
        var userCareer = response.getBody();
        log.info("userCareer={}", userCareer);
        return userCareer;
    }

    public UserCareerDto defaultUserCareer(Exception e) {
        return UserCareerDto.builder()
                .id(0)
                .skills(Collections.emptyList())
                .build();
    }
}
