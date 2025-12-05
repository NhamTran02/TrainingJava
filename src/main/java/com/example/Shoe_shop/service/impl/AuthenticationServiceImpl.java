package com.example.Shoe_shop.service.impl;
import com.example.Shoe_shop.dto.request.AuthenticationRequest;
import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.response.AuthenticationResponse;
import com.example.Shoe_shop.entity.Role;
import com.example.Shoe_shop.entity.Token;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.UserRepository;
import com.example.Shoe_shop.service.AuthenticationService;
import com.example.Shoe_shop.service.EmailService;
import com.example.Shoe_shop.service.JwtService;
import com.example.Shoe_shop.service.TokenService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    JwtService jwtService;
    TokenService tokenService;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    EntityValidatorUtil entityValidatorUtil;
    EmailService emailService;


    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        List<Role> roles=request.getRoleIds().stream()
                .map(entityValidatorUtil::requireRole)
                .toList();
        String verificationCode= UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .roles(roles)
                .verified(false)
                .verificationCode(verificationCode)
                .deleted(false)
                .build();

        userRepository.save(user);
        //gửi mail xác minh email
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest req) {
        // Kiểm tra user tồn tại với username
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Authenticate với password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        // Kiểm tra user đã verify chưa
        if(!Boolean.TRUE.equals(user.getVerified())) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        Token token = tokenService.findByUsername(user.getUsername())
                .orElse(new Token());

        token.setUsername(user.getUsername());
        token.setAccessToken(access);
        token.setRefreshToken(refresh);
        token.setBlacklisted(false);
        tokenService.save(token);

        return AuthenticationResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .userId(user.getId())
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refresh(String refreshToken) {
        // validate signature + expiry
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("INVALID_REFRESH_TOKEN");
        }
        // check DB: phải tồn tại và chưa blacklist
        Token dbToken = tokenService.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("REFRESH_NOT_FOUND"));

        if (dbToken.isBlacklisted()) {
            throw new RuntimeException("REFRESH_REVOKED");
        }

        String username = jwtService.getUsernameFromRefreshToken(refreshToken);
        User user = entityValidatorUtil.requireUserName(username);

        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        Token newToken = tokenService.findByUsername(user.getUsername())
                .orElseGet(Token::new);
        newToken.setUsername(user.getUsername());
        newToken.setAccessToken(newAccess);
        newToken.setRefreshToken(newRefresh);
        tokenService.save(newToken);

        return AuthenticationResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .userId(user.getId())
                .build();
    }

    @Override
    @Transactional
    public void logout(String tokenValue) {
        tokenService.findByRefreshToken(tokenValue).ifPresent(token -> {
            token.setBlacklisted(true);
            tokenService.save(token);
        });
    }

    @Override
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);
            user.setPasswordHash(passwordEncoder.encode(tempPassword));
            userRepository.save(user);

            emailService.sendTemporaryPassword(user.getEmail(), tempPassword);
        });
    }
}
