package com.example.domain.todo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository<T extends Todo> extends JpaRepository<T, Long>, TodoRepositoryCustom {
}
