package com.example.service;

import com.example.controller.dto.member.MemberResponseDto;
import com.example.controller.dto.workspace.WorkspaceResponseDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.MemberNotFoundException;
import com.example.exception.WorkspaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long saveWorkspace(WorkspaceSaveRequestDto rq) {
        Long userId = rq.getUserId();
        String name = rq.getName();

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("Could not found member with id " + userId));

        Participant participant = Participant.create(member);

        Workspace workspace = Workspace.create(name, participant);
        workspaceRepository.save(workspace);
        return workspace.getId();
    }

    @Transactional
    public void addParticipants(AddParticipantsRequestDto rq) {
        Long workspaceId = rq.getWorkspaceId();

        Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
                .orElseThrow(() -> new WorkspaceNotFoundException("Could not found workspace with id " + workspaceId));

        List<Member> members = memberRepository.findAllById(rq.getAccountIds());

        workspace.addParticipants(members);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDto findById(Long workspaceId) {
        return new WorkspaceResponseDto(workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Could not found workspace with id " + workspaceId)));
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto> findAllByMemberId(Long memberId) {
        return workspaceRepository.findAllByMemberId(memberId).stream()
                .map(WorkspaceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMembersById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId);
        return workspace.getParticipantGroup().getParticipants().stream()
                .map(p -> p.getMember())
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long workspaceId) {
        workspaceRepository.deleteById(workspaceId);
    }

    @Transactional
    public void deleteParticipantByMemberId(Long memberId, Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId);
        if (workspace.getParticipantGroup().isExistByMemberId(memberId)) {
            workspace.getParticipantGroup().removeParticipant(memberId);
        }
    }
}
