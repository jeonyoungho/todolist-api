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
    @Column(name = "member_id")
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

    //== 생성 메서드 ==//
    public static Member create(String accountId, String accountPw, String name, String city, String street, String zipcode, Authority authority) {
        Address address = Address.create(city, street, zipcode);
        return create(accountId, accountPw, name, address, authority);
    }

    public static Member create(String accountId, String accountPw, String name, Address address, Authority authority) {
        return Member.builder()
                .accountId(accountId)
                .accountPw(accountPw)
                .name(name)
                .address(address)
                .authority(authority)
                .build();
    }

}
