package com.example.domain.workspace;

import com.example.domain.TestQuerydslConfig;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Import(TestQuerydslConfig.class)
@RunWith(SpringRunner.class)
@DataJpaTest
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
    public void findAllByMemberId_ValidInput_Success() {
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
    public void findAllByMemberId_NotExistedWorkspaces_IsEmpty() {
        // given
        memberRepository.save(member);

        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberId(null);

        // then
        assertThat(workspaces.size()).isEqualTo(0);
    }

    @Test
    public void findAllByMemberId_WorkspaceIdIsNull_IsEmpty() {
        // given
        memberRepository.save(member);

        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberId(null);

        // then
        assertThat(workspaces.size()).isEqualTo(0);
    }

    @Test
    public void findByIdWithFetchJoinParticipantMember_ValidInput_Success() {
        // given
        memberRepository.save(member);

        final String testWorkspaceName= "test-workspace";
        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create(testWorkspaceName, participant);
        workspaceRepository.save(workspace);

        // when
        Workspace result = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspace.getId());

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);
        assertThat(result.getParticipantGroup().getSize()).isEqualTo(1);
    }

}