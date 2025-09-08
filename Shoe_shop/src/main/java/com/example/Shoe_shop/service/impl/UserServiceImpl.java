package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.UserCreateDTO;
import com.example.Shoe_shop.entity.Role;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.RoleRepository;
import com.example.Shoe_shop.repository.UserRepository;
import com.example.Shoe_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Override
    public User createUser(UserCreateDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_DOES_NOT_EXTS));
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        else if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        } else if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
        User user= User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(dto.getPassword())
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .role(role)
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }
}
