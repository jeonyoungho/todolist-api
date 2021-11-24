package com.example.domain.workspace;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.factory.UserFactory;
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
public class ParticipantGroupTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void isExistByMemberId_GivenExistedMemberId_True() throws Exception {
        // given
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        ParticipantGroup participantGroup = createParticipantGroup(member);

        // when
        Boolean expected = participantGroup.isExistByMemberId(member.getId());

        // then
        Assertions.assertThat(expected).isTrue();
    }



    @Test
    public void isExistByMemberId_GivenNotExistedMemberId_False() throws Exception {
        // given
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        ParticipantGroup participantGroup = createParticipantGroup(member);

        // when
        Boolean expected = participantGroup.isExistByMemberId(1000L);

        // then
        Assertions.assertThat(expected).isFalse();
    }

    private ParticipantGroup createParticipantGroup(Member member) {
        Participant participant = Participant.create(member);
        ParticipantGroup participantGroup = new ParticipantGroup();
        participantGroup.addParticipant(participant);
        return participantGroup;
    }

}