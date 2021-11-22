package com.example.service;

import com.example.controller.dto.user.UserResponseDto;
import com.example.controller.dto.workspace.WorkspaceResponseDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.user.User;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.user.UserRepository;
import com.example.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveWorkspace(WorkspaceSaveRequestDto rq) {
        Long memberId = rq.getMemberId();
        String name = rq.getName();

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found member with id " + memberId));

        Participant participant = Participant.create(user);

        Workspace workspace = Workspace.create(name, participant);

        return workspaceRepository.save(workspace).getId();
    }

    @Transactional
    public void addParticipants(AddParticipantsRequestDto rq) {
        Long workspaceId = rq.getWorkspaceId();

        Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Could not found workspace with id " + workspaceId));

        List<User> users = userRepository.findAllById(rq.getMemberIds());

        workspace.addParticipants(users);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDto findById(Long workspaceId) {
        return new WorkspaceResponseDto(workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found workspace with id " + workspaceId)));
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto> findAllByMemberId(Long memberId) {
        return workspaceRepository.findAllByMemberId(memberId).stream()
                .map(WorkspaceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findMembersById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId);
        return workspace.getParticipantGroup().getParticipants().stream()
                .map(p -> p.getUser())
                .map(UserResponseDto::new)
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
