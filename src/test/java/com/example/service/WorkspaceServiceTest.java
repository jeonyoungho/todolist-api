package com.example.service;

import com.example.controller.dto.member.MemberResponseDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceResponseDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorkspaceServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    WorkspaceService workspaceService;
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
    public void saveWorkspace_Basic_Success() {
        // given
        memberRepository.save(member);

        final String testWorkspaceName = "test-workspace-name";
        WorkspaceSaveRequestDto requestDto = WorkspaceSaveRequestDto.create(member.getId(), testWorkspaceName);

        // when
        Long workspaceId = workspaceService.saveWorkspace(requestDto);
        Workspace result = workspaceRepository.findById(workspaceId).get();

        // then
        assertThat(workspaceId).isGreaterThanOrEqualTo(0L);
        assertThat(result.getName()).isEqualTo(testWorkspaceName);
    }

    @Test
    public void addParticipants_Basic_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        List<Long> memberIds = new ArrayList<>();
        for(int i=0;i<20;i++) {
            Member saveMember = Member.create("test-id" + i, "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
            memberRepository.save(saveMember);
            memberIds.add(saveMember.getId());
        }

        // when
        workspaceService.addParticipants(AddParticipantsRequestDto.create(workspace.getId(), memberIds));

        Workspace result = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspace.getId());
        List<Participant> participants = result.getParticipantGroup().getParticipants();

        // then
         assertThat(participants.size()).isEqualTo(21); // 처음에 만든 사람 1 + 참가자 20
    }

    @Test
    public void findById_Basic_Success() {
        // given
        memberRepository.save(member);

        final String testWorkspaceName = "test-workspace-name";
        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create(testWorkspaceName, participant);
        workspaceRepository.save(workspace);

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspace.getId());

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);
    }

    @Test
    public void deleteParticipantByMemberId_GivenExistedMember_True() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace-name", participant);
        workspaceRepository.save(workspace);

        // when
        workspaceService.deleteParticipantByMemberId(member.getId(), workspace.getId());
        Workspace result = workspaceRepository.findById(workspace.getId()).get();

        // then
        assertThat(result.getParticipantGroup().getSize()).isEqualTo(0);
    }

    @Test
    public void findMembersById_MembersExist_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace-name", participant);

        List<Member> members = new ArrayList<>();
        for(int i=0;i<20;i++) {
            Member saveMember = Member.create("test-id" + i, "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
            memberRepository.save(saveMember);
            members.add(saveMember);
        }

        workspace.addParticipants(members);
        workspaceRepository.save(workspace);

        // when
        List<MemberResponseDto> results = workspaceService.findMembersById(workspace.getId());

        // then
        assertThat(results.size()).isEqualTo(21);
    }

}