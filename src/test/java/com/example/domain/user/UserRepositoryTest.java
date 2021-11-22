package com.example.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void save_GivenValidInput_Success() {
        // given
        User user = createUser();
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findById(user.getId()).get();

        // then
        assertThat(findUser).isEqualTo(saveUser);
    }

    @Test
    public void findById_GivenInValidInput_Fail() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findById(500L);

        // then
        assertThat(result.isPresent()).isFalse();
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
}