package com.example.service;

import com.example.controller.dto.member.MemberSignUpRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    public void signUp_GivenValidInput_Success() throws Exception {
        // given
        MemberSignUpRequestDto request = MemberSignUpRequestDto.builder()
                .accountId("signUpMember1")
                .accountPw("signUpMember1pw")
                .name("alice")
                .city("city")
                .street("street")
                .zipcode("zipcode")
                .build();
        
        // when
        Long savedId = memberService.signUp(request);

        // then
        Assertions.assertThat(savedId).isGreaterThanOrEqualTo(0L);
    }

}