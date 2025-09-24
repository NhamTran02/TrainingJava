package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends BaseAudit {

    @NotBlank(message = "USER_NAME_INVALID")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    String username;

    @Email(message = "EMAIL_INVALID")
    @Column(unique = true, nullable = false)
    String email;

    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 6, message = "PASSWORD_INVALID")
    @Column(nullable = false, name = "password_hash")
    String passwordHash;

    @Column(nullable = false, name = "full_name")
    @NotBlank(message = "FULL_NAME_INVALID")
    String fullName;

    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^0[0-9]{9}$", message = "PHONE_INVALID")
    @Column(nullable = false, name = "phone_number")
    String phoneNumber;

    @NotBlank(message = "ADDRESS_INVALID")
    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    Boolean deleted = false;

    @Column(nullable = false)
    Boolean verified = false;

    @Column(name = "verification_code")
    String verificationCode;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Cart cart;

    @OneToMany(mappedBy = "user")
    List<Order> orders;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<Wishlist> wishlists;
}
