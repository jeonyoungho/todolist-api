package com.example.repository;

import com.example.domain.Address;
import com.example.domain.Member;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired MemberRepository memberRepository;

    @Test
    public void saveTest() {
        Member member = Member.builder()
                .userId("aliceid")
                .password("alicepw")
                .username("alice")
                .address(Address.builder()
                        .street("street1")
                        .city("city1")
                        .zipcode("zipcode1")
                        .build())
                .build();

        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember).isEqualTo(saveMember);
    }


}