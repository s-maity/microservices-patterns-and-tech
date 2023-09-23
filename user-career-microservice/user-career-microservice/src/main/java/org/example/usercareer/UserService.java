package org.example.usercareer;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService {

    public UserDto getUserCareerDetailsById(int id) {

        someTimeConsumingProcess();

        return UserDto.builder()
                .id(id)
                .skills(Arrays.asList("Java", "Spring boot", "Microservices"))
                .build();
    }

    private static void someTimeConsumingProcess() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
