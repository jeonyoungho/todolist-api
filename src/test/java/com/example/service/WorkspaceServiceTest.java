package com.example.service;

import com.example.domain.Participant;
import com.example.domain.Workspace;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkspaceServiceTest {

    @Autowired WorkspaceService workspaceService;

    @Test
    public void saveWorkspaceTest() throws Exception {
        // given
        Participant[] participants = new Participant[1];
        participants[0] = new Participant(null, null);

        Workspace ws = Workspace.create("alice", participants);

        // when

        // then
    }
}