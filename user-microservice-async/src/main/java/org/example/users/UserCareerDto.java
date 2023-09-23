package org.example.users;

import lombok.Builder;

import java.util.List;

@Builder
public record UserCareerDto(int id, List<String> skills) {
}
