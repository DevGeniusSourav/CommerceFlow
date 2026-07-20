package com.commerceflow.userservice.service.impl;

import com.commerceflow.userservice.dtos.request.CreateUserRequest;
import com.commerceflow.userservice.dtos.response.UserResponse;
import com.commerceflow.userservice.entity.User;
import com.commerceflow.userservice.exception.UserAlreadyExistsException;
import com.commerceflow.userservice.mapper.UserMapper;
import com.commerceflow.userservice.repository.UserRepository;
import com.commerceflow.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse registerUser(CreateUserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.email())) {
            throw new UserAlreadyExistsException(userRequest.email());
        }

        User user = userMapper.toUser(userRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
