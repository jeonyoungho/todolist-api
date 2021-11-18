package com.example.service;

import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.ParticipantGroup;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import org.assertj.core.api.Assertions;
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

    @Autowired EntityManager em;
    @Autowired WorkspaceService workspaceService;
    @Autowired WorkspaceRepository workspaceRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    public void saveWorkspace_basic_success() throws Exception {
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
    public void addParticipants_basic_success() throws Exception {
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
        for(int i=0;i<10;i++) {
            Member saveMember = Member.builder()
                    .userId("test-id")
                    .password("test-pw")
                    .username("test-user")
                    .address(Address.builder()
                            .street("test-street")
                            .city("test-city")
                            .zipcode("test-zipcode")
                            .build())
                    .build();
            memberRepository.save(saveMember);
            memberIds.add(saveMember.getId());
        }

        em.flush();
        em.clear();

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
        Workspace result = workspaceRepository.findByIdWithFetchJoinParticipants(workspaceId);
        List<Participant> participantGroup = result.getParticipantGroup().getParticipants();



        // then
        // Assertions.assertThat(workspaces.size()).isEqualTo(10);


    }
}