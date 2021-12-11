package com.example.domain.workspace;

import com.example.config.TestQuerydslConfig;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Import(TestQuerydslConfig.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class WorkspaceRepositoryTest {

    @Autowired
    EntityManager em;
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
            Workspace workspace = Workspace.create("test-workspace" + i, member);
            workspaceRepository.save(workspace);
        }

        em.flush();
        em.clear();

        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberIdFetchJoinParticipant(member.getId());

        // then
        assertThat(workspaces.size()).isEqualTo(10);
    }

    @Test
    public void findAllByMemberId_NotExistedWorkspaces_IsEmpty() {
        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberIdFetchJoinParticipant(member.getId());

        // then
        Assertions.assertAll(
                () -> assertThat(workspaces).isNotNull(),
                () -> assertThat(workspaces.size()).isEqualTo(0)
        );
    }

    @Test
    public void findAllByMemberId_WorkspaceIdIsNull_IsEmpty() {
        // when
        List<Workspace> workspaces = workspaceRepository.findAllByMemberIdFetchJoinParticipant(null);

        // then
        Assertions.assertAll(
                () -> assertThat(workspaces).isNotNull(),
                () -> assertThat(workspaces.size()).isEqualTo(0)
        );
    }

    @Test
    public void findByIdFetchJoinParticipantAndMember_ValidInput_Success() {
        // given
        memberRepository.save(member);

        Member member2 = Member.create("test-id2", "test-pw", "test-name2", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        memberRepository.save(member2);

        final String testWorkspaceName= "test-workspace";
        Workspace workspace = Workspace.create(testWorkspaceName, member);
        workspace.addParticipant(member2);
        workspaceRepository.save(workspace);

        em.flush();
        em.clear();

        // when
        Workspace result = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspace.getId()).get();

        // then
        Assertions.assertAll(
                () -> assertThat(result.getName()).isEqualTo(testWorkspaceName),
                () -> assertThat(result.getParticipantGroup().getSize()).isEqualTo(2)
        );
    }

}
