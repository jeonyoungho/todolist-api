package com.example.domain.workspace;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.domain.member.QMember.member;
import static com.example.domain.workspace.QParticipant.participant;
import static com.example.domain.workspace.QWorkspace.workspace;

@RequiredArgsConstructor
public class WorkspaceRepositoryImpl implements WorkspaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Workspace> findAllByMemberId(Long memberId) {
        return queryFactory
                .select(workspace).distinct()
                .from(workspace)
                .leftJoin(workspace.participantGroup.participants, participant).fetchJoin()
                .where(participantMemberIdEq(memberId))
                .fetch();
    }

    @Override
    public Workspace findByIdWithFetchJoinParticipantAndMember(Long workspaceId) {
        return queryFactory
                .select(workspace).distinct()
                .from(workspace)
                .leftJoin(workspace.participantGroup.participants, participant).fetchJoin()
                .leftJoin(participant.member, member).fetchJoin()
                .where(workspaceIdEq(workspaceId))
                .fetchOne();
    }

    private BooleanExpression participantMemberIdEq(Long memberId) {
        return memberId != null ? participant.member.id.eq(memberId) : null;
    }

    private BooleanExpression workspaceIdEq(Long workspaceId) {
        return workspaceId != null ? workspace.id.eq(workspaceId) : null;
    }
}