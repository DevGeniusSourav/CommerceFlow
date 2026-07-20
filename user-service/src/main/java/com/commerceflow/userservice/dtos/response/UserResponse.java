package com.commerceflow.userservice.dtos.response;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {}