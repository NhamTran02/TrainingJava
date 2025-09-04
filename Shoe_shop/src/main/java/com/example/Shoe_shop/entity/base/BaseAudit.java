package com.example.Shoe_shop.entity.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseAudit extends BaseId{
    private LocalDateTime createTime;
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
