package com.example.domain.member;

import com.example.config.CustomDataJpaTest;
import com.example.config.TestQuerydslConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(TestQuerydslConfig.class)
@RunWith(SpringRunner.class)
@CustomDataJpaTest
public class MemberRepositoryTest {

    @Autowired
    EntityManager em;
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
        Member savedMember = memberRepository.save(member);

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertAll(
                () -> assertThat(findMember).isNotNull(),
                () -> assertThat(findMember.getAccountId()).isEqualTo(savedMember.getAccountId()),
                () -> assertThat(findMember.getAccountPw()).isEqualTo(savedMember.getAccountPw()),
                () -> assertThat(findMember.getName()).isEqualTo(savedMember.getName())
        );
    }

    @Test
    public void findById_NotExistedMemberId_False() {
        // when
        Optional<Member> result = memberRepository.findById(500L);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findByAccountId_ValidInput_True() {
        // given
        Member savedMember = memberRepository.save(member);

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findByAccountId(savedMember.getAccountId()).get();

        // then
        assertAll(
                () -> assertThat(findMember.getAccountId()).isEqualTo(savedMember.getAccountId()),
                () -> assertThat(findMember.getAccountPw()).isEqualTo(savedMember.getAccountPw()),
                () -> assertThat(findMember.getName()).isEqualTo(savedMember.getName())
        );
    }

    @Test
    public void findByAccountId_NotExistedAccountId_False() {
        // when
        Optional<Member> result = memberRepository.findByAccountId("fake-id");

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void existsByAccountId_ExistedAccountId_True() {
        // given
        memberRepository.save(member);

        em.flush();
        em.clear();

        // when
        boolean result = memberRepository.existsByAccountId(member.getAccountId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void existsByAccountId_NotExistedAccountId_False() {
        // when
        boolean result = memberRepository.existsByAccountId("fake-id");

        // then
        assertThat(result).isFalse();
    }

}
