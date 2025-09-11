package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import com.example.Shoe_shop.utils.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "roles")
public class Role extends BaseId {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true,name = "role_name")
    RoleName roleName;

    @OneToMany(mappedBy = "role")
    @JsonManagedReference
    List<User> users;
}
