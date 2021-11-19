package com.example.service;

import com.example.controller.dto.member.MemberResponseDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceResponseDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.ParticipantGroup;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class WorkspaceServiceTest {

    @Autowired EntityManager em;
    @Autowired WorkspaceService workspaceService;
    @Autowired WorkspaceRepository workspaceRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void saveWorkspace_Basic_Success() throws Exception {
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

        final String testWorkspaceName= "test-workspace";
        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .memberId(member.getId())
                .name(testWorkspaceName)
                .build());

        // when
        Workspace result = workspaceRepository.findById(workspaceId).get();

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);
    }

    @Test
    public void addParticipants_Basic_Success() throws Exception {
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

        List<Long> memberIds = new ArrayList<>();
        for(int i=0;i<20;i++) {
            Member saveMember = Member.builder()
                    .userId("test-id" + i)
                    .password("test-pw" + i)
                    .username("test-user" + i)
                    .address(Address.builder()
                            .street("test-street" + i)
                            .city("test-city" + i)
                            .zipcode("test-zipcode" + i)
                            .build())
                    .build();
            memberRepository.save(saveMember);
            memberIds.add(saveMember.getId());
        }

        final String testWorkspaceName= "test-workspace";
        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .memberId(member.getId())
                .name(testWorkspaceName)
                .build());

        workspaceService.addParticipants(AddParticipantsRequestDto.builder()
                .workspaceId(workspaceId)
                .memberIds(memberIds)
                .build());

        em.flush();
        em.clear();

        // when
        Workspace result = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId);
        List<Participant> participants = result.getParticipantGroup().getParticipants();

        // then
//         Assertions.assertThat(participants.size()).isEqualTo(21); // 처음에 만든 사람 1 + 참가자 20

//        for (Participant participant : participants) {
//            System.out.println("participant.getMember().getUsername() = " + participant.getMember().getUsername());
//        }
    }

    @Test
    public void findById_Basic_Success() throws Exception {
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

        final String testWorkspaceName= "test-workspace";
        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .memberId(member.getId())
                .name(testWorkspaceName)
                .build());

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspaceId);

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);
    }

    @Test
    public void deleteParticipantByMemberId_GivenExistedMember_True() throws Exception {
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

        final String testWorkspaceName= "test-workspace";
        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .memberId(member.getId())
                .name(testWorkspaceName)
                .build());

        // when
        workspaceService.deleteParticipantByMemberId(member.getId(), workspaceId);
        Workspace result = workspaceRepository.findById(workspaceId).get();

        // then
        assertThat(result.getParticipantGroup().getSize()).isEqualTo(0);
    }

    @Test
    public void findMembersById_MembersExist_Success() throws Exception {
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

        List<Long> memberIds = new ArrayList<>();
        for(int i=0;i<20;i++) {
            Member saveMember = Member.builder()
                    .userId("test-id" + i)
                    .password("test-pw" + i)
                    .username("test-user" + i)
                    .address(Address.builder()
                            .street("test-street" + i)
                            .city("test-city" + i)
                            .zipcode("test-zipcode" + i)
                            .build())
                    .build();
            memberRepository.save(saveMember);
            memberIds.add(saveMember.getId());
        }

        final String testWorkspaceName= "test-workspace";
        Long workspaceId = workspaceService.saveWorkspace(WorkspaceSaveRequestDto.builder()
                .memberId(member.getId())
                .name(testWorkspaceName)
                .build());

        workspaceService.addParticipants(AddParticipantsRequestDto.builder()
                .workspaceId(workspaceId)
                .memberIds(memberIds)
                .build());

        em.flush();
        em.clear();

        // when
        List<MemberResponseDto> results = workspaceService.findMembersById(workspaceId);

//        for (MemberResponseDto memberResponseDto : results) {
//            System.out.println("memberResponseDto = " + memberResponseDto);
//        }

        // then
        assertThat(results.size()).isEqualTo(21);
    }
}