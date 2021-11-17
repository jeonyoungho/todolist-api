package com.example.service;

import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.Member;
import com.example.domain.Participant;
import com.example.domain.ParticipantGroup;
import com.example.domain.Workspace;
import com.example.repository.MemberRepository;
import com.example.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    public Long save(WorkspaceSaveRequestDto request) {
        Long memberId = request.getMemberId();
        String name = request.getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found member with id " + memberId));

        Participant participant = Participant.create(member);

        Workspace workspace = Workspace.create(name, participant);

        return workspaceRepository.save(workspace).getId();
    }
}
