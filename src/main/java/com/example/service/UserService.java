package com.example.service;

import com.example.controller.dto.user.UserSignUpDto;
import com.example.domain.user.Address;
import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import com.example.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User signUp(final UserSignUpDto rq) {
        Address address = Address.builder()
                .street(rq.getStreet())
                .city(rq.getCity())
                .zipcode(rq.getZipcode())
                .build();

        final User user = User.builder()
                .accountId(rq.getAccountId())
                .accountPw(passwordEncoder.encode(rq.getAccountPw()))
                .name(rq.getName())
                .address(address)
                .role(UserRole.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByAccountId(final String accountId) {
        return userRepository.findByAccountId(accountId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
