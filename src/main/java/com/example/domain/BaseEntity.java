package com.example.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(name = "created_account_id", updatable = false)
    private String createdAccountId; // 유저의 계정 아이디

    @LastModifiedBy
    @Column(name = "last_modified_account_id")
    private String lastModifiedAccountId; // 유저의 계정 패스워드
}
