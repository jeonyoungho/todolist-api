package com.example.domain.workspace;

import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorkspaceRepositoryTest {

    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    MemberRepository memberRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void findAllByMemberId_Basic_Success() {
        // given
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
    }


    @Test
    public void findByIdWithFetchJoinParticipantMember_Basic_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);

        final String testWorkspaceName= "test-workspace";
        Workspace workspace = Workspace.create(testWorkspaceName, participant);
        workspaceRepository.save(workspace);

        // when
        Workspace result = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspace.getId());

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);
        assertThat(result.getParticipantGroup().getSize()).isEqualTo(1);
    }

}