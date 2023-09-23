package org.example.userdetails;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(int id, String name, String org) {
}
