package com.example.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Getter
@NoArgsConstructor
@Embeddable
public class ParticipantGroup {

    @OneToMany(mappedBy = "workspace", cascade = ALL)
    private List<Participant> participants = new ArrayList<>();

    public ParticipantGroup(List<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }
}
