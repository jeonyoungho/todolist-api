package com.example;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.user.Address;
import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import com.example.domain.todo.TodoRepository;
import com.example.domain.user.UserRole;
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
        private final UserRepository userRepository;
        private final WorkspaceService workspaceService;
        private final TodoRepository todoRepository;
        private final TodoService todoService;
        private final BCryptPasswordEncoder passwordEncoder;

        public void dbInit1() {
            User user = createUser();
            userRepository.save(user);

            List<Long> memberIds = new ArrayList<>();
            for(int i=0;i<20;i++) {
                User saveUser = User.builder()
                        .accountId("test-id" + i)
                        .accountPw(passwordEncoder.encode("test-pw" + i))
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
        }

        private User createUser() {
            return User.builder()
                    .accountId("test-id")
                    .accountPw(passwordEncoder.encode("test-pw"))
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
}

