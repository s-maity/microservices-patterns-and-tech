package org.example.usercareer;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(int id, List<String> skills) {
}
