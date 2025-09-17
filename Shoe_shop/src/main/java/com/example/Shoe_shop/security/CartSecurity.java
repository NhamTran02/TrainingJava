package com.example.Shoe_shop.security;

import com.example.Shoe_shop.repository.CartJdbcRepository;
import com.example.Shoe_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component()
@RequiredArgsConstructor
public class CartSecurity {
    private final CartJdbcRepository cartJdbcRepository;
    private final UserRepository userRepository;

    public boolean hasAccess(Authentication authentication, Long cartId) {
        // Nếu là ADMIN → luôn có quyền
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Lấy userId từ principal trong JWT
        String username = authentication.getName();
        Long userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        // Lấy cartId của user đang đăng nhập
        Long ownerCartId = cartJdbcRepository.getCartIdByUserId(userId);

        return ownerCartId.equals(cartId);
    }
}
