package com.example.domain.workspace;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.domain.member.QUser.user;
import static com.example.domain.workspace.QParticipant.*;
import static com.example.domain.workspace.QWorkspace.workspace;

@RequiredArgsConstructor
public class WorkspaceRepositoryImpl implements WorkspaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Workspace> findAllByMemberId(Long memberId) {
        return queryFactory
                .select(workspace).distinct()
                .from(workspace)
                .leftJoin(workspace.participantGroup.participants, participant).fetchJoin()
                .where(participant.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public Workspace findByIdWithFetchJoinParticipantAndMember(Long workspaceId) {
        return queryFactory
                .select(workspace).distinct()
                .from(workspace)
                .leftJoin(workspace.participantGroup.participants, participant).fetchJoin()
                .leftJoin(participant.member, user).fetchJoin()
                .where(workspace.id.eq(workspaceId))
                .fetchOne();
    }

}