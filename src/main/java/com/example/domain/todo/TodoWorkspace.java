package com.example.domain.todo;

import com.example.domain.workspace.Workspace;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TodoWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_workspace_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Builder
    public TodoWorkspace(Todo todo, Workspace workspace) {
        this.todo = todo;
        this.workspace = workspace;
    }

    //== 생성 메서드 ==//
    public static TodoWorkspace create(Workspace workspace) {
        return TodoWorkspace.builder()
                .workspace(workspace)
                .build();
    }

    //== 연관관계 메서드 ==//
    public void setTodo(Todo todo) {
        this.todo = todo;
    }
}
