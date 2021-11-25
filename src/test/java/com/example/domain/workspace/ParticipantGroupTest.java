package com.example.domain.workspace;

import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ParticipantGroupTest {

    @Autowired
    MemberRepository memberRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void isExistByMemberId_GivenExistedMemberId_True() throws Exception {
        // given
        memberRepository.save(member);

        ParticipantGroup participantGroup = createParticipantGroup(member);

        // when
        Boolean expected = participantGroup.isExistByMemberId(member.getId());

        // then
        assertThat(expected).isTrue();
    }

    @Test
    public void isExistByMemberId_GivenNotExistedMemberId_False() throws Exception {
        // given
        memberRepository.save(member);

        ParticipantGroup participantGroup = createParticipantGroup(member);

        // when
        Boolean expected = participantGroup.isExistByMemberId(1000L);

        // then
        assertThat(expected).isFalse();
    }

    private ParticipantGroup createParticipantGroup(Member member) {
        Participant participant = Participant.create(member);
        ParticipantGroup participantGroup = new ParticipantGroup();
        participantGroup.addParticipant(participant);
        return participantGroup;
    }

}