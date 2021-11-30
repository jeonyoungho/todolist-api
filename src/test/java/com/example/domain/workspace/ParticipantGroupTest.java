package com.example.domain.workspace;

import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantGroupTest {

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    public void isExistByMemberId_GivenExistedMemberId_True() {
        // given
        ParticipantGroup participantGroup = createParticipantGroup(member);

        // when
        Boolean expected = participantGroup.isExistByMemberId(member.getId());

        // then
        assertThat(expected).isTrue();
    }

    @Test
    public void isExistByMemberId_GivenNotExistedMemberId_False() {
        // given
        ParticipantGroup participantGroup = createParticipantGroup(member);

        // when
        Boolean expected = participantGroup.isExistByMemberId(555L);

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