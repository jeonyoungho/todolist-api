package com.example.domain.workspace;

import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.factory.UserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorkspaceRepositoryTest {

    @Autowired EntityManager em;
    @Autowired WorkspaceRepository workspaceRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void findAllByMemberId_Basic_Success() {
        // given
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        for (int i = 0; i < 10; i++) {
            Participant participant = Participant.create(member);
            Workspace workspace = Workspace.create("test-workspace" + i, participant);
            workspaceRepository.save(workspace);
        }

        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberId(member.getId());

        // then
        assertThat(workspaces.size()).isEqualTo(10);

//        System.out.println("workspaces = " + workspaces.size());
//        System.out.println("=============");
//        for (Workspace ws : workspaces) {
//            System.out.println("id: " + ws.getId());
//            System.out.println("name = " + ws.getName());
//            System.out.println("workspace = " + ws.getParticipantGroup().getParticipants().size());
//        }
    }


    @Test
    public void findByIdWithFetchJoinParticipantMember_Basic_Success() throws Exception {
        // given
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        Participant participant = Participant.create(member);

        final String testWorkspaceName= "test-workspace";
        Workspace workspace = Workspace.create(testWorkspaceName, participant);
        workspaceRepository.save(workspace);

        em.flush();
        em.clear();

        // when
        Workspace result = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspace.getId());

        // then
        assertThat(result.getParticipantGroup().getSize()).isEqualTo(1);
    }

}