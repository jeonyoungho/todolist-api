package com.example.domain.user;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_account_id", length = 20, nullable = false, unique = true)
    private String accountId;

    @Column(name = "user_account_pw", length = 100, nullable = false)
    private String accountPw;

    @Column(name = "user_name", length = 20, nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column(name = "enable", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isEnable = true; // 사용 여부

    @Column(name = "user_role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public User(String accountId, String accountPw, String name, Address address, UserRole role) {
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.name = name;
        this.address = address;
        this.role = role;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
