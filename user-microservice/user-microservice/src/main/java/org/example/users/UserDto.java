package org.example.users;

import lombok.Builder;

import java.util.List;
@Builder
public record UserDto(int id, String name, String org, List<String> skills) {
}
