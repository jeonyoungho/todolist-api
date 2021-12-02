package com.example;

import com.example.domain.member.Address;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.Todo;
import com.example.domain.todo.TodoRepository;
import com.example.domain.todo.TodoWorkspace;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit0(); // account id string
        initService.dbInit1(0, "workspace1"); // workspace1 -> member1~10 (총 10명) -> member1 상위 todo 1개 하위 todo 1개 작성
        initService.dbInit1(10, "workspace2"); // workspace2 -> member11~20 (총 10명) -> member2 상위 todo 1개 하위 todo 1개 작성
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final MemberRepository memberRepository;
        private final WorkspaceRepository workspaceRepository;
        private final TodoRepository todoRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private Member member;

        public void dbInit0() {
            member = Member.create("string", passwordEncoder.encode("string"), "string", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
            memberRepository.save(member);
        }

        public void dbInit1(int idx, String workspaceName) {
            Workspace workspace = Workspace.create(workspaceName, Participant.create(member));

            List<Member> members = new ArrayList<>();
            for(int i=idx+1; i<=idx+10; i++) {
                Member saveMember = Member.create("member" + i, passwordEncoder.encode("string"), "member" + i, "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
                memberRepository.save(saveMember);
                members.add(saveMember);
            }

            workspace.addParticipants(members);
            workspaceRepository.save(workspace);


            Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "parent-todo-test-content", null, 10);
            todoRepository.save(parentTodo);

            for (int i=0; i<10; i++) {
                Todo childTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child-todo-test-content" + i, parentTodo, 20);
                todoRepository.save(childTodo);
            }

        }

        private Member createMember() {
            return Member.builder()
                    .accountId("test-id")
                    .accountPw(passwordEncoder.encode("string"))
                    .name("test-user")
                    .address(Address.builder()
                            .street("test-street")
                            .city("test-city")
                            .zipcode("test-zipcode")
                            .build())
                    .authority(Authority.ROLE_USER)
                    .build();
        }

    }
}

