package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.UserUpdateDTO;
import com.example.Shoe_shop.dto.response.UserResponse;
import com.example.Shoe_shop.entity.Role;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.UserMapper;
import com.example.Shoe_shop.repository.UserRepository;
import com.example.Shoe_shop.service.UserService;
import com.example.Shoe_shop.utils.CheckRole;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    EntityValidatorUtil  entityValidatorUtil;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        List<User> users = CheckRole.isAdmin()
                ? userRepository.findAll()
                : userRepository.findAllByDeletedFalse();
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user= entityValidatorUtil.requireUser(id);

        if (!CheckRole.isAdmin() && Boolean.TRUE.equals(user.getDeleted())){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = entityValidatorUtil.requireUser(id);
        if (userUpdateDTO.getPhoneNumber() != null) {
            userRepository.findByPhoneNumber(userUpdateDTO.getPhoneNumber())
                    .filter(u -> !u.getId().equals(id)) // loại bỏ chính user đang update
                    .ifPresent(u -> {
                        throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
                    });
        }

        userMapper.updateUserFromDtoExcludingSensitive(userUpdateDTO, user);

        if(CheckRole.isAdmin()){
            if(userUpdateDTO.getPassword()!=null){
                user.setPasswordHash(passwordEncoder.encode(userUpdateDTO.getPassword()));
            }
            if(userUpdateDTO.getRoleId()!=null){
                Role role=entityValidatorUtil.requireRole(userUpdateDTO.getRoleId());
                user.setRole(role);
            }

        }
        return userMapper.toUserResponse(userRepository.save(user));
    }


    @Override
    @Transactional
    public void softDeleteUser(Long id) {
        User user= entityValidatorUtil.requireUser(id);
        if (user.getDeleted()){
            throw new AppException(ErrorCode.USER_ALREADY_DELETED);
        }
        user.setDeleted(true);
        userRepository.save(user);
    }

}
