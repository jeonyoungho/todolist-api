package com.example.domain.todo;

import com.example.domain.todo.TodoWorkspace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TodoWorkspaceGroup {

    @OneToMany(mappedBy = "todo")
    private List<TodoWorkspace> todoWorkspaces = new ArrayList<>();

    public void addTodoWorkspaces(TodoWorkspace[] todoWorkspaces) {
        this.todoWorkspaces.addAll(Arrays.asList(todoWorkspaces));
    }

}
