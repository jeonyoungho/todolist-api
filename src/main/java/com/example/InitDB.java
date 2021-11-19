package com.example;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.TodoFactory;
import com.example.domain.todo.TodoRepository;
import com.example.service.TodoService;
import com.example.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

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

        public void dbInit1() {
            Member member = createMember();
            memberRepository.save(member);

            List<Long> memberIds = new ArrayList<>();
            for(int i=0;i<20;i++) {
                Member saveMember = Member.builder()
                        .userId("test-id" + i)
                        .password("test-pw" + i)
                        .username("test-user" + i)
                        .address(Address.builder()
                                .street("test-street" + i)
                                .city("test-city" + i)
                                .zipcode("test-zipcode" + i)
                                .build())
                        .build();
                memberRepository.save(saveMember);
                memberIds.add(saveMember.getId());
            }

            final String testWorkspaceName= "test-workspace";
            Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                    .memberId(member.getId())
                    .name(testWorkspaceName)
                    .build());

            workspaceService.addParticipants(AddParticipantsRequestDto.builder()
                    .workspaceId(workspaceId)
                    .memberIds(memberIds)
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

        private Member createMember() {
            return Member.builder()
                    .userId("test-id")
                    .password("test-pw")
                    .username("test-user")
                    .address(Address.builder()
                            .street("test-street")
                            .city("test-city")
                            .zipcode("test-zipcode")
                            .build())
                    .build();
        }



    }
}

