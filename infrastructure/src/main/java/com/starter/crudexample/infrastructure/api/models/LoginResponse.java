package com.starter.crudexample.infrastructure.api.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("user_id") String userId,
    @JsonProperty("username") String username,
    @JsonProperty("email") String email,
    @JsonProperty("roles") List<String> roles
) {
    public LoginResponse(String accessToken, String userId, String username, String email, List<String> roles) {
        this(accessToken, "Bearer", userId, username, email, roles);
    }
}
