package com.commerceflow.userservice.mapper;

import com.commerceflow.userservice.dtos.request.CreateUserRequest;
import com.commerceflow.userservice.dtos.response.UserResponse;
import com.commerceflow.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(CreateUserRequest userRequest){
        return User.create(userRequest.firstName(), userRequest.lastName(), userRequest.email(),  userRequest.password());
    }

    public UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
