package com.example.api.service;

import com.example.api.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.TodoRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TodoServiceIntegrationTest {

    @Autowired
    EntityManager em;
    @Autowired
    TodoService todoService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    TodoRepository<com.example.domain.todo.Todo> todoRepository;

    @Test
    public void saveBasicTodo_ValidInput_Success() throws Exception {
        // given
        Member member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        memberRepository.save(member);

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        final String content = "todo-test-content";
        BasicTodoSaveRequestDto request = BasicTodoSaveRequestDto.create(member.getId(), workspace.getId(), content, null, 10);

        // when
        Long result = todoService.saveBasicTodo(request);
        BasicTodo basicTodo = (BasicTodo) todoRepository.findById(result).get();

        // then
        assertThat(basicTodo.getContent()).isEqualTo(content);
    }

//    @Test
//    @Rollback(value = false)
//    public void test() {
//        // given
//        Member member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
//        memberRepository.save(member);
//
//        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
//        UserDetails principal = new User(member.getAccountId(), "", authorities);
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
//
//        Participant participant = Participant.create(member);
//        Workspace workspace = Workspace.create("test-workspace", participant);
//        workspaceRepository.save(workspace);
//
//        final String content = "todo-test-content";
//        BasicTodo todo = BasicTodo.createBasicTodo(member, workspace, content, null, 100);
//        todoRepository.save(todo);
//
//        em.flush();
//        em.clear();
//
//        // when
//        Long result = workspaceRepository.countByIdAndCurrentAccountId(workspace.getId(), member.getAccountId());
//
//        System.out.println("result = " + result);
//
//        // then
//    }

}