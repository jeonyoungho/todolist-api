package com.example.domain.workspace;

import com.example.domain.BaseEntity;
import com.example.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@ToString(of = {"id", "name", "participantGroup"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Workspace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Embedded
    private ParticipantGroup participantGroup = new ParticipantGroup();

    @Builder
    public Workspace(String name, Member member) {
        this.name = name;
        addParticipant(member);
    }

    //== 생성 메서드 ==//
    public static Workspace create(String name, Member member) {
        return Workspace.builder()
                .name(name)
                .member(member)
                .build();
    }

    //== 연관관계 메서드 ==//
    public void addParticipant(Member member) {
        Participant participant = Participant.create(member);
        participantGroup.addParticipant(participant);
        participant.setWorkspace(this);
    }

    public void addParticipants(List<Member> members) {
        for (Member member : members) {
            addParticipant(member);
        }
    }

    public void removeParticipant(Long memberId) {
        participantGroup.removeParticipant(memberId);
    }

    public List<Participant> getParticipants() {
        return getParticipantGroup().getParticipants();
    }

    public Boolean isExistByAccountId(String accountId) {
        return participantGroup.isExistByAccountId(accountId);
    }


}
