package com.example.domain.member;

import com.example.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "account_id", length = 20, nullable = false, unique = true)
    private String accountId;

    @Column(name = "account_pw", length = 100, nullable = false)
    private String accountPw;

    @Column(length = 20, nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column(name = "enable", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isEnable = true; // 사용 여부

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String accountId, String accountPw, String name, Address address, Authority authority) {
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.name = name;
        this.address = address;
        this.authority = authority;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public void setRole(Authority authority) {
        this.authority = authority;
    }
}
