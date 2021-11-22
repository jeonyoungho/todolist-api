package com.example.domain.workspace;

import com.example.domain.user.Address;
import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import com.example.domain.user.UserRole;
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
    UserRepository userRepository;

    @Test
    public void isExistByMemberId_GivenExistedMemberId_True() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        ParticipantGroup participantGroup = createParticipatnGroup(user);

        // when
        Boolean expected = participantGroup.isExistByMemberId(user.getId());

        // then
        Assertions.assertThat(expected).isTrue();
    }



    @Test
    public void isExistByMemberId_GivenNotExistedMemberId_False() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        ParticipantGroup participantGroup = createParticipatnGroup(user);

        // when
        Boolean expected = participantGroup.isExistByMemberId(1000L);

        // then
        Assertions.assertThat(expected).isFalse();
    }

    private User createUser() {
        return User.builder()
                .accountId("test-id")
                .accountPw("test-pw")
                .name("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .role(UserRole.ROLE_USER)
                .build();
    }

    private ParticipantGroup createParticipatnGroup(User user) {
        Participant participant = Participant.create(user);
        ParticipantGroup participantGroup = new ParticipantGroup();
        participantGroup.addParticipant(participant);
        return participantGroup;
    }

}