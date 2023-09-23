package org.example.users;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserDetailsAggregateService userDetailsAggregateService;
    private final UserCareerService userCareerService;

    public UserDto getUserById(int id) {

        //Circuit breaker
        var userDetails = userDetailsAggregateService.getUserDetailsUsingRestTemplate(id);

        //Retry
        var userCareer = userDetailsAggregateService.getUserCareerUsingRestTemplate(id);

        //TODO: reteLimiter
        var status = "Active"; // userDetailsAggregateService.getUserStatus(id);
        return build(userDetails, userCareer, status);
    }

    private UserDto build(UserDetailsDto user, UserCareerDto career, String status) {
        return UserDto.builder()
                .id(user.id())
                .name(user.name())
                .org(user.org())
                .status(status)
                .skills(user.id() != 0 ? career.skills() : Collections.emptyList())
                .build();
    }
}
