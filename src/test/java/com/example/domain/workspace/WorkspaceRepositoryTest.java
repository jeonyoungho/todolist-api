package com.example.domain.workspace;

import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorkspaceRepositoryTest {

    @Autowired EntityManager em;
    @Autowired WorkspaceRepository workspaceRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void findAllByMemberId_basic_success() {
        // given
        Member member = Member.builder()
                .userId("test-id")
                .password("test-pw")
                .username("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .build();
        memberRepository.save(member);

        for (int i = 0; i < 10; i++) {
            Participant participant = Participant.create(member);
            Workspace workspace = Workspace.create("test-workspace" + i, participant);
            workspaceRepository.save(workspace);
        }

        em.flush();
        em.clear();

        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberId(member.getId());

        // then
        Assertions.assertThat(workspaces.size()).isEqualTo(10);

//        System.out.println("workspaces = " + workspaces.size());
//        System.out.println("=============");
//        for (Workspace ws : workspaces) {
//            System.out.println("id: " + ws.getId());
//            System.out.println("name = " + ws.getName());
//            System.out.println("workspace = " + ws.getParticipantGroup().getParticipants().size());
//        }
    }


}