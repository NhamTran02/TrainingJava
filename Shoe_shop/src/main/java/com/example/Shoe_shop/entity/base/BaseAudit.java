package com.example.Shoe_shop.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseAudit extends BaseId{
    @Column(name = "created_at")
    private LocalDateTime createTime;

    @Column(name = "updated_at")
    private LocalDateTime updateTime;

    @PrePersist
    public void onCreate(){
        createTime=LocalDateTime.now();
        updateTime=LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        updateTime=LocalDateTime.now();
    }

}
