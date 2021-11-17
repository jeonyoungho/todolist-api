package com.example.domain.todo;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@DiscriminatorValue("basic")
@Entity
public class BasicTodo extends Todo {
    @Column(nullable = false)
    private int expectedTime;
}
