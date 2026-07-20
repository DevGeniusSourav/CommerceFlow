package com.commerceflow.userservice.service;

import com.commerceflow.userservice.dtos.request.CreateUserRequest;
import com.commerceflow.userservice.dtos.response.UserResponse;

public interface UserService {

    UserResponse registerUser(CreateUserRequest userRequest);
}
