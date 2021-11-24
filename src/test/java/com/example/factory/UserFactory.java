package com.example.factory;

import com.example.domain.member.Address;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;

public class UserFactory {

    private static int userNumber = 0;

    public synchronized static Member createUser() {
        return Member.builder()
                .accountId("test-id" + userNumber++)
                .accountPw("test-pw")
                .name("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .authority(Authority.ROLE_USER)
                .build();
    }

}
