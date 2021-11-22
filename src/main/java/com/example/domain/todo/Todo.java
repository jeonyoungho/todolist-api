package com.example.domain.todo;

import com.example.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(of = {"id", "member", "content", "todoWorkspaceGroup", "parent"})
public abstract class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @Column(length = 50, nullable = false)
    private String content;

    @Embedded
    private TodoWorkspaceGroup todoWorkspaceGroup = new TodoWorkspaceGroup();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Todo parent;

    @OneToMany(mappedBy = "parent", cascade = ALL, orphanRemoval = true)
    private Set<Todo> childs = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TodoStatus status;

    public Todo(User user, TodoWorkspace todoWorkspace, String content, Todo parent, TodoStatus status) {
        this.user = user;
        this.content = content;
        this.parent = parent;
        this.status = status;

        addTodoWorkspace(todoWorkspace);
    }

    //== 연관관계 메서드 ==//
    public void addTodoWorkspace(TodoWorkspace todoWorkspace) {
        todoWorkspaceGroup.addTodoWorkspace(todoWorkspace);
        todoWorkspace.setTodo(this);
    }

    public void changeStatus(TodoStatus status) {
        this.status = status;
    }

    public Boolean isAllChildCompleted() {
        return childs.stream().allMatch(t -> TodoStatus.COMPLETED.equals(t.status));
    }

    public void clearChilds() {
        getChilds().clear();
    }

    public int childsSize() {
        return getChilds().size();
    }
}
