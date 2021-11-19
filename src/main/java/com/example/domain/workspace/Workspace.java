package com.example.domain.workspace;

import com.example.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@ToString(of = {"id", "name", "participantGroup"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Embedded
    private ParticipantGroup participantGroup = new ParticipantGroup();

    @Builder
    public Workspace(String name, Participant... participants) {
        this.name = name;
        if (participants != null && participants.length > 0) {
            for(Participant participant : participants) {
                addParticipant(participant);
            }
        }
    }

    //== 생성 메서드 ==//
    public static Workspace create(String name, Participant... participants) {
        return Workspace.builder()
                .name(name)
                .participants(participants)
                .build();
    }

    //== 연관관계 메서드 ==//
    public void addParticipant(Participant participant) {
        participantGroup.addParticipant(participant);
        participant.setWorkspace(this);
    }

    public void addParticipants(List<Member> members) {
        for (Member member : members) {
            Participant participant = Participant.create(member);
            addParticipant(participant);
        }
    }

}
