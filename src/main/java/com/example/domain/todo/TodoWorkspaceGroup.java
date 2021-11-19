package com.example.domain.todo;

import com.example.domain.todo.TodoWorkspace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TodoWorkspaceGroup {

    @OneToMany(mappedBy = "todo", cascade = ALL, orphanRemoval = true)
    private List<TodoWorkspace> todoWorkspaces = new ArrayList<>();

    public void addTodoWorkspace(TodoWorkspace todoWorkspace) {
        this.todoWorkspaces.add(todoWorkspace);
    }

    public void addTodoWorkspaces(TodoWorkspace... todoWorkspaces) {
        this.todoWorkspaces.addAll(Arrays.asList(todoWorkspaces));
    }

}
