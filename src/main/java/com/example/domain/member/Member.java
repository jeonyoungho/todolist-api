package com.example.domain.member;

import com.example.domain.member.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(name = "user_pw", length = 20, nullable = false)
    private String password;

    @Column(name = "user_name", length = 10, nullable = false)
    private String username;

    @Embedded
    private Address address;

    @Builder
    public Member(String userId, String password, String username, Address address) {
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.address = address;
    }
}
