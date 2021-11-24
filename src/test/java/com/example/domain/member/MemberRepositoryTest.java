package com.example.domain.member;

import com.example.factory.UserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void save_GivenValidInput_Success() {
        // given
        Member member = UserFactory.createUser();
        Member saveMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    public void findById_GivenInValidInput_Fail() throws Exception {
        // given
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        // when
        Optional<Member> result = memberRepository.findById(500L);

        // then
        assertThat(result.isPresent()).isFalse();
    }


}