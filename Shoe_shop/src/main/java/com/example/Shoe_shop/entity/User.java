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

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends BaseAudit {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50)
    @Column(unique = true,nullable = false)
    String username;

    @Email(message = "Email không hợp lệ")
    @Column(unique = true,nullable = false)
    String email;

    @NotBlank
    @Size(min = 6, message = "mật khẩu phải có ít nhất 6 kí tự")
    @Column(nullable = false,name = "password_hash")
    String passwordHash;

    @Column(nullable = false,name = "full_name")
    String fullName;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải 10 số")
    @Column(nullable = false,name = "phone_number")
    String phoneNumber;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    Boolean deleted = false;

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
