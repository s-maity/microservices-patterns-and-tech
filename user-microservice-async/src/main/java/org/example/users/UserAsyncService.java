package org.example.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserAsyncService {

    private final String BASE_URL = "http://api-gateway/api/v1/";

    @Autowired
    private RestTemplate restTemplate;

    @Async
    public CompletableFuture<UserDetailsDto> getUserDetailsUsingRestTemplate(int id) {
        ResponseEntity<UserDetailsDto> response
                = restTemplate.getForEntity(BASE_URL + "users/" + id, UserDetailsDto.class);

        var userDetails = response.getBody();
        log.info("user={}", userDetails);
        return CompletableFuture.completedFuture(userDetails);
    }

    @Async
    public CompletableFuture<UserCareerDto> getUserCareerUsingRestTemplate(int id) {
        ResponseEntity<UserCareerDto> response
                = restTemplate.getForEntity(BASE_URL + "user-career/" + id, UserCareerDto.class);

        var userCareer = response.getBody();
        log.info("userCareer={}", userCareer);
        return CompletableFuture.completedFuture(userCareer);
    }
}
