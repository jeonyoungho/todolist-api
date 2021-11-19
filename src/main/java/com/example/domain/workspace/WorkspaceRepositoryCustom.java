package com.example.domain.workspace;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkspaceRepositoryCustom {
    List<Workspace> findAllByMemberId(Long memberId);
    Workspace findByIdWithFetchJoinParticipantAndMember(Long workspaceId);
}
