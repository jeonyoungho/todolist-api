package com.example.api.service;

import com.example.api.dto.member.MemberResponseDto;
import com.example.api.dto.workspace.AddParticipantsRequestDto;
import com.example.api.dto.workspace.WorkspaceResponseDto;
import com.example.api.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
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
@Transactional(readOnly = true)
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long saveWorkspace(WorkspaceSaveRequestDto rq) {
        Member member = memberRepository.findById(rq.getMemberId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        SecurityUtil.checkValidRequest(member.getAccountId());

        Workspace workspace = Workspace.create(rq.getWorkspaceName(), member);
        workspaceRepository.save(workspace);

        return workspace.getId();
    }

    @Transactional
    public void addParticipants(AddParticipantsRequestDto rq) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())
                        .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        if (!workspace.isExistByAccountId(SecurityUtil.getCurrentAccountId())) {
            throw new CustomException(INVALID_REQUEST);
        }

        List<Member> members = memberRepository.findAllById(rq.getAccountIds());

        workspace.addParticipants(members);
    }

    public WorkspaceResponseDto findById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        return new WorkspaceResponseDto(workspace);
    }

    public List<WorkspaceResponseDto> findAllByMemberId(Long memberId) {
        return workspaceRepository.findAllByMemberIdFetchJoinParticipant(memberId).stream()
                .map(WorkspaceResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<MemberResponseDto> findMembersById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        List<Participant> participants = workspace.getParticipants();
        return participants.stream()
                .map(p -> p.getMember())
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        if (!workspace.isExistByAccountId(SecurityUtil.getCurrentAccountId())) {
            throw new CustomException(INVALID_REQUEST);
        }

        workspaceRepository.delete(workspace);
    }

    @Transactional
    public void deleteParticipantByMemberId(Long memberId, Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        if (!workspace.isExistByAccountId(SecurityUtil.getCurrentAccountId())) {
            throw new CustomException(INVALID_REQUEST);
        }

        workspace.removeParticipant(memberId);
    }
}
