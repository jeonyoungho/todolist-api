package com.example.api.service;

import com.example.api.dto.member.MemberResponseDto;
import com.example.api.dto.workspace.AddParticipantsRequestDto;
import com.example.api.dto.workspace.WorkspaceResponseDto;
import com.example.api.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.ParticipantGroup;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.CustomException;
import com.example.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long saveWorkspace(WorkspaceSaveRequestDto rq) {
        Member member = memberRepository.findById(rq.getMemberId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        SecurityUtil.checkAuthority(member.getAccountId());

        Participant participant = Participant.create(member);

        Workspace workspace = Workspace.create(rq.getName(), participant);
        workspaceRepository.save(workspace);

        return workspace.getId();
    }

    @Transactional
    public void addParticipants(AddParticipantsRequestDto rq) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId());

        validateWorkspaceAuthority(workspace);

        List<Member> members = memberRepository.findAllById(rq.getAccountIds());

        workspace.addParticipants(members);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDto findById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        return new WorkspaceResponseDto(workspace);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto> findAllByMemberId(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        return workspaceRepository.findAllByMemberIdFetchJoinParticipant(memberId).stream()
                .map(WorkspaceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMembersById(Long workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new CustomException(WORKSPACE_NOT_FOUND);
        }

        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId);
        List<Participant> participants = workspace.getParticipantGroup().getParticipants();
        return participants.stream()
                .map(p -> p.getMember())
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId);

        validateWorkspaceAuthority(workspace);

        workspaceRepository.delete(workspace);
    }

    @Transactional
    public void deleteParticipantByMemberId(Long memberId, Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId);

        validateWorkspaceAuthority(workspace);

        workspace.getParticipantGroup().removeParticipant(memberId);
    }

    private void validateWorkspaceAuthority(Workspace workspace) {
        if (workspace == null) {
            throw new CustomException(WORKSPACE_NOT_FOUND);
        }

        ParticipantGroup participantGroup = workspace.getParticipantGroup();
        if (!participantGroup.isExistByAccountId(SecurityUtil.getCurrentAccountId())) {
            throw new CustomException(UNAUTHORIZED_MEMBER);
        }
    }
}
