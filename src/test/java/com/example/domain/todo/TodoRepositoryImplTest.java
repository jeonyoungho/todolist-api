package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.user.Address;
import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import com.example.domain.user.UserRole;
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
    UserRepository userRepository;
    @Autowired
    WorkspaceService workspaceService;

    @Test
    @Rollback(value = false)
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_GivenValidInput_Success() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        List<Long> memberIds = new ArrayList<>();
        for(int i=0;i<20;i++) {
            User saveUser = User.builder()
                    .accountId("test-id" + i)
                    .accountPw("test-pw" + i)
                    .name("test-user" + i)
                    .address(Address.builder()
                            .street("test-street" + i)
                            .city("test-city" + i)
                            .zipcode("test-zipcode" + i)
                            .build())
                    .role(UserRole.ROLE_USER)
                    .build();
            userRepository.save(saveUser);
            memberIds.add(saveUser.getId());
        }

        final String testWorkspaceName= "test-workspace";
        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .memberId(user.getId())
                .name(testWorkspaceName)
                .build());

        workspaceService.addParticipants(AddParticipantsRequestDto.builder()
                .workspaceId(workspaceId)
                .memberIds(memberIds)
                .build());

        final String parentContent = "parent-todo-test-content";
        BasicTodoSaveRequestDto parentRequest = BasicTodoSaveRequestDto.builder()
                .memberId(user.getId())
                .workspaceId(workspaceId)
                .content(parentContent)
                .parentId(null)
                .expectedTime(10)
                .build();

        Long parentBasicTodoId = todoService.saveBasicTodo(parentRequest);

        final String childContent = "child-todo-test-content";
        BasicTodoSaveRequestDto childRequest = BasicTodoSaveRequestDto.builder()
                .memberId(user.getId())
                .workspaceId(workspaceId)
                .content(childContent)
                .parentId(parentBasicTodoId)
                .expectedTime(20)
                .build();

        Long childBasicTodoId = todoService.saveBasicTodo(childRequest);

        final String grandChildContent = "grand-child-todo-test-content";
        BasicTodoSaveRequestDto grandChildRequest = BasicTodoSaveRequestDto.builder()
                .memberId(user.getId())
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

        // then
    }

    private User createUser() {
        return User.builder()
                .accountId("test-id")
                .accountPw("test-pw")
                .name("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .role(UserRole.ROLE_USER)
                .build();
    }
}