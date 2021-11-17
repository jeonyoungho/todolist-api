package com.example.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Builder
    public Participant(Member member, Workspace workspace) {
        this.member = member;
        this.workspace = workspace;
    }

    //== 생성 메서드 ==//
    public static Participant create(Member member) {
        return Participant.builder()
                .member(member)
                .build();
    }

    //== 변경 메서드 ==//
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
