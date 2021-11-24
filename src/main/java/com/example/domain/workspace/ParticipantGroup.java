package com.example.domain.workspace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@ToString(of = {"participants"})
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class ParticipantGroup {

    @OneToMany(mappedBy = "workspace", cascade = ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public ParticipantGroup(List<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(Long memberId) {
        Participant participant = findByMemberId(memberId);
        participants.remove(participant);
    }

    public int getSize() {
        return participants.size();
    }

    public Participant findByMemberId(Long memberId) {
        return participants.stream()
                .parallel()
                .filter(p -> p.getMember().getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not found participant with id " + memberId));
    }

    public Boolean isExistByMemberId(Long memberId) {
        return participants.stream()
                .anyMatch(p -> memberId.equals(p.getMember().getId()));
    }

}
