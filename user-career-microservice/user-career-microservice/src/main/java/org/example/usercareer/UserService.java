package org.example.usercareer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RefreshScope
public class UserService {

    @Value("${app.microservices.message: Have patience}")
    private String message;

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

    public String getMessage() {
        return message;
    }
}
