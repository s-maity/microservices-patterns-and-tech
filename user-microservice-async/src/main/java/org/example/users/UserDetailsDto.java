package org.example.users;

import lombok.Builder;

@Builder
public record UserDetailsDto(int id, String name, String org) {
}
