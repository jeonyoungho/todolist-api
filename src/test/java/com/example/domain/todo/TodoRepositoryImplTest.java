package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.factory.UserFactory;
import com.example.service.TodoService;
import com.example.service.WorkspaceService;
import org.assertj.core.api.Assertions;
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
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        List<Long> memberIds = new ArrayList<>();
        for(int i=0;i<20;i++) {
            Member saveMember = UserFactory.createUser();
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