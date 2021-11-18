package com.example.service;

import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceListResponseDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    public Long saveWorkspace(WorkspaceSaveRequestDto rq) {
        Long memberId = rq.getMemberId();
        String name = rq.getName();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found member with id " + memberId));

        Participant participant = Participant.create(member);

        Workspace workspace = Workspace.create(name, participant);

        return workspaceRepository.save(workspace).getId();
    }

    public Long addParticipants(AddParticipantsRequestDto rq) {
        Long workspaceId = rq.getWorkspaceId();

        Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Could not found workspace with id " + workspaceId));

        List<Member> members = memberRepository.findAllById(rq.getMemberIds());

        workspace.addParticipants(members);

        return workspace.getId();
    }

    public List<WorkspaceListResponseDto> findAllByMemberId(Long memberId) {
        return workspaceRepository.findAllByMemberId(memberId).stream()
                .map(WorkspaceListResponseDto::new)
                .collect(Collectors.toList());
    }
}
