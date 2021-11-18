package com.example.domain.todo;

import com.example.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public abstract class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "todo_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private TodoWorkspaceGroup workspaceGroup;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TodoStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Todo parent;

    @OneToMany(mappedBy = "parent")
    private List<Todo> childs = new ArrayList<>();

//    @Builder
//    public Todo(String content, Member member, TodoWorkspaceGroup workspaceGroup, TodoStatus status, Todo parent) {
//        this.content = content;
//        this.member = member;
//        this.workspaceGroup = workspaceGroup;
//        this.status = status;
//        this.parent = parent;
//    }
//
//    //== 생성 메서드 ==//
//    public static Todo create(String content, Member member, Todo parent, TodoWorkspace... todoWorkspaces) {
//        Todo todo = Todo.builder()
//                .content(content)
//                .member(member)
//                .status(TodoStatus.UNCOMPLETED)
//                .parent(parent)
//                .build();
//         todo.workspaceGroup.addTodoWorkspaces(todoWorkspaces);
//
//         return todo;
//    }
}
