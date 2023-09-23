package org.example.userdetails;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService {

    public UserDto getUserById(int id) {
        someTimeConsumingProcess();
        return UserDto.builder()
                .name("Sudipta Maity")
                .id(id)
                .org("SG")
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
