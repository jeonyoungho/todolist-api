package com.example.domain.todo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class TodoWorkspaceGroup {

    @OneToMany(mappedBy = "todo", cascade = ALL, orphanRemoval = true)
    private Set<TodoWorkspace> todoWorkspaces = new LinkedHashSet<>();

    //== 연관관계 메서드 ==//
    public void addTodoWorkspace(TodoWorkspace todoWorkspace) {
        this.todoWorkspaces.add(todoWorkspace);
    }

}
