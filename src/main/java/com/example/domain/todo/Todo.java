package com.example.domain.todo;

import com.example.domain.BaseEntity;
import com.example.domain.member.Member;
import com.example.domain.workspace.Workspace;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public abstract class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "todo_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

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

    public Todo(Member member, Workspace workspace, String content, Todo parent, TodoStatus status) {
        this.member = member;
        this.content = content;
        this.parent = parent;
        this.status = status;

        addTodoWorkspace(workspace);
    }

    //== 연관관계 메서드 ==//
    public void addTodoWorkspace(Workspace workspace) {
        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);
        todoWorkspaceGroup.addTodoWorkspace(todoWorkspace);
        todoWorkspace.setTodo(this);
    }

    public void changeStatus(TodoStatus status) {
        this.status = status;
    }

    public Boolean isAllChildsCompleted() {
        return childs.stream().allMatch(t -> TodoStatus.COMPLETED.equals(t.status));
    }

    public Boolean hasChilds() {
        return getChilds().size() > 0;
    }

    public void clearChilds() {
        if (hasChilds()) {
            getChilds().clear();
        }
    }

    public Boolean hasParent() {
        return parent != null;
    }
}
