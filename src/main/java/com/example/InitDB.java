package com.example;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.TodoRepository;
import com.example.domain.member.Authority;
import com.example.service.TodoService;
import com.example.service.WorkspaceService;
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
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final MemberRepository memberRepository;
        private final WorkspaceService workspaceService;
        private final TodoRepository todoRepository;
        private final TodoService todoService;
        private final BCryptPasswordEncoder passwordEncoder;

        public void dbInit1() {
            Member member = createUser();
            memberRepository.save(member);

            List<Long> memberIds = new ArrayList<>();
            for(int i=0;i<20;i++) {
                Member saveMember = Member.builder()
                        .accountId("test-id" + i)
                        .accountPw(passwordEncoder.encode("test-pw" + i))
                        .name("test-user" + i)
                        .address(Address.builder()
                                .street("test-street" + i)
                                .city("test-city" + i)
                                .zipcode("test-zipcode" + i)
                                .build())
                        .authority(Authority.ROLE_USER)
                        .build();
                memberRepository.save(saveMember);
                memberIds.add(saveMember.getId());
            }

            final String testWorkspaceName= "test-workspace";
            Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                    .userId(member.getId())
                    .name(testWorkspaceName)
                    .build());

            workspaceService.addParticipants(AddParticipantsRequestDto.builder()
                    .workspaceId(workspaceId)
                    .accountIds(memberIds)
                    .build());

            final String parentContent = "parent-todo-test-content";
            BasicTodoSaveRequestDto parentRequest = BasicTodoSaveRequestDto.builder()
                    .memberId(member.getId())
                    .workspaceId(workspaceId)
                    .content(parentContent)
                    .parentId(null)
                    .expectedTime(10)
                    .build();

            Long parentBasicTodoId = todoService.saveBasicTodo(parentRequest);

            final String childContent = "child-todo-test-content";
            BasicTodoSaveRequestDto childRequest = BasicTodoSaveRequestDto.builder()
                    .memberId(member.getId())
                    .workspaceId(workspaceId)
                    .content(childContent)
                    .parentId(parentBasicTodoId)
                    .expectedTime(20)
                    .build();

            Long childBasicTodoId = todoService.saveBasicTodo(childRequest);
        }

        private Member createUser() {
            return Member.builder()
                    .accountId("test-id")
                    .accountPw(passwordEncoder.encode("test-pw"))
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

