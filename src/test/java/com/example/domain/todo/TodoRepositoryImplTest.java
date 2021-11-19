package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Workspace;
import com.example.service.TodoService;
import com.example.service.WorkspaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TodoRepositoryImplTest {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    EntityManager em;
    @Autowired
    TodoService todoService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WorkspaceService workspaceService;

    @Test
    @Rollback(value = false)
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_GivenValidInput_Success() throws Exception {
        // given
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

        final String grandChildContent = "grand-child-todo-test-content";
        BasicTodoSaveRequestDto grandChildRequest = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .workspaceId(workspaceId)
                .content(grandChildContent)
                .parentId(parentBasicTodoId)
                .expectedTime(20)
                .build();

        Long grandChildBasicTodoId = todoService.saveBasicTodo(grandChildRequest);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinTodoWorkspaceGroupAndChilds(parentBasicTodoId);

        Set<Todo> childs = result.getChilds();
        for (Todo child : childs) {
            System.out.println("child.getContent() = " + child.getContent());
        }
        System.out.println();

        // then
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