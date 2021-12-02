package com.example.domain.member;

import com.example.config.CustomDataJpaTest;
import com.example.config.TestQuerydslConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestQuerydslConfig.class)
@RunWith(SpringRunner.class)
@CustomDataJpaTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void save_ValidInput_Success() {
        // given
        Member saveMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(saveMember.getId()).get();

        // then
        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    public void findById_NotExistedMemberId_False() {
        // given
        memberRepository.save(member);

        // when
        Optional<Member> result = memberRepository.findById(500L);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findByAccountId_ValidInput_True() {
        // given
        memberRepository.save(member);

        // when
        Optional<Member> result = memberRepository.findByAccountId(member.getAccountId());

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void findByAccountId_NotExistedAccountId_False() {
        // given
        memberRepository.save(member);

        // when
        Optional<Member> result = memberRepository.findByAccountId("fake-id");

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void existsByAccountId_ExistedAccountId_True() {
        // given
        memberRepository.save(member);

        // when
        boolean result = memberRepository.existsByAccountId(member.getAccountId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void existsByAccountId_NotExistedAccountId_False() {
        // given
        memberRepository.save(member);

        // when
        boolean result = memberRepository.existsByAccountId("fake-id");

        // then
        assertThat(result).isFalse();
    }

}