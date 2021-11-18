package com.example.domain.member;

import com.example.domain.member.Address;
import com.example.domain.member.Member;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.domain.member.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void save_basic_success() {
        Member member = Member.builder()
                .userId("test-id")
                .password("test-pw")
                .username("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .build();

        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember).isEqualTo(saveMember);
    }
}