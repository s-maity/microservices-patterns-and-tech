package org.example.userdetails;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserDetailsController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") int id) {
        var user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> getUserStatus(@PathVariable("id") int id) {
        var status = id % 2 == 0 ? "ACTIVE" : "INACTIVE";
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
