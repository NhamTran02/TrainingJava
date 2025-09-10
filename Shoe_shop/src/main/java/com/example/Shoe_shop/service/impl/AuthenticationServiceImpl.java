package com.example.Shoe_shop.service.impl;
import com.example.Shoe_shop.dto.request.AuthenticationRequest;
import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.response.AuthenticationResponse;
import com.example.Shoe_shop.entity.Role;
import com.example.Shoe_shop.entity.Token;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.RoleRepository;
import com.example.Shoe_shop.repository.UserRepository;
import com.example.Shoe_shop.service.AuthenticationService;
import com.example.Shoe_shop.service.JwtService;
import com.example.Shoe_shop.service.TokenService;
import com.example.Shoe_shop.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService {
    final UserService userService;
    final JwtService jwtService;
    final TokenService tokenService;
    final AuthenticationManager authenticationManager;
    final PasswordEncoder passwordEncoder;
    final UserRepository userRepository;
    final RoleRepository roleRepository;


    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(role)
                .deleted(false)
                .build();

        User savedUser = userRepository.save(user);

        return AuthenticationResponse.builder()
                .userId(savedUser.getId())
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest req) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

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
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

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
    public void logout(String tokenValue) {
        tokenService.findByRefreshToken(tokenValue).ifPresent(token -> {
            token.setBlacklisted(true);
            tokenService.save(token);
        });
        tokenService.findByAccessToken(tokenValue).ifPresent(token -> {
            token.setBlacklisted(true);
            tokenService.save(token);
        });
    }
}
