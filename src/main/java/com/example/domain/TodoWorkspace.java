package com.example.domain;

import com.example.domain.todo.Todo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TodoWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "todo_workspace_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

}
