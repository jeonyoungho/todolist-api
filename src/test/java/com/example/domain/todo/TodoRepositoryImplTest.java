package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.service.TodoService;
import com.example.service.WorkspaceService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_GivenValidInput_Success() {
        // given
        memberRepository.save(member);

        List<Long> memberIds = new ArrayList<>();
        for(int i=0;i<20;i++) {
            Address address = Address.create("test-city", "test-street", "test-zipcode");
            Member saveMember = Member.create("test-id" + i, "test-pw", "test-name", address, Authority.ROLE_USER);
            memberRepository.save(saveMember);
            memberIds.add(saveMember.getId());
        }

        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .userId(member.getId())
                .name("test-workspace")
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
//        for (Todo child : childs) {
//            System.out.println("child.getContent() = " + child.getContent());
//        }

        // then
        Assertions.assertThat(childs.size()).isEqualTo(2);
    }
}