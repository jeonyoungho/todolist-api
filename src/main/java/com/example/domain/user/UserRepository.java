package com.example.domain.user;

import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByAccountIdAndAccountPw(String accountId, String accountPw);
    Optional<User> findByAccountId(String accountId);
}