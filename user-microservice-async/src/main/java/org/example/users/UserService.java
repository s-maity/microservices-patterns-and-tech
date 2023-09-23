package org.example.users;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserAsyncService userAsyncService;


    public UserDto getUserById(int id) {

        var userDetails = userAsyncService.getUserDetailsUsingRestTemplate(id);
        var userCareer = userAsyncService.getUserCareerUsingRestTemplate(id);

        CompletableFuture.allOf(userDetails, userCareer)
                .join();

        return build(userDetails.join(), userCareer.join());
    }

    private UserDto build(UserDetailsDto user, UserCareerDto career) {
        return UserDto.builder()
                .id(user.id())
                .name(user.name())
                .org(user.org())
                .skills(career.skills())
                .build();
    }
}
