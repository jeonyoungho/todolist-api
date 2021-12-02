package com.example.domain.workspace;

import java.util.List;

public interface WorkspaceRepositoryCustom {
    List<Workspace> findAllByMemberIdFetchJoinParticipant(Long memberId);
    Workspace findByIdFetchJoinParticipantAndMember(Long workspaceId);
}
