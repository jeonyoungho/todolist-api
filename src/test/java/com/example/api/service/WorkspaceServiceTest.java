package com.example.api.service;

import com.example.api.dto.member.MemberResponseDto;
import com.example.api.dto.workspace.AddParticipantsRequestDto;
import com.example.api.dto.workspace.WorkspaceResponseDto;
import com.example.api.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.CustomException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceServiceTest {

    @InjectMocks
    WorkspaceService workspaceService;
    @Mock
    WorkspaceRepository workspaceRepository;
    @Mock
    MemberRepository memberRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    @Test
    public void saveWorkspace_ValidInput_Success() {
        // given
        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(any());
        
        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        verify(memberRepository).findById(rq.getMemberId());
        verify(memberRepository, times(1)).findById(rq.getMemberId());

        verify(workspaceRepository).save(any(Workspace.class));
        verify(workspaceRepository, times(1)).save(any(Workspace.class));
    }

    @Test(expected = CustomException.class)
    public void saveWorkspace_NotExistedMember_ThrowCustomException() {
        // given
        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.empty());

        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        fail("회원 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void saveWorkspace_UnauthorizedMember_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));

        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        fail("작업 공간 등록 권한 체크시 예외가 발생해야 합니다.");
    }

    @Test
    public void addParticipants_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long workspaceId = 1L;
        List<Long> accountIds = Arrays.asList(new Long[]{new Long(1)});
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.create(workspaceId, accountIds);

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())).thenReturn(workspace);
        when(memberRepository.findAllById(rq.getAccountIds())).thenReturn(anyList());

        // when
        workspaceService.addParticipants(rq);

        // then
        verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId());
        verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId());

        verify(memberRepository).findAllById(rq.getAccountIds());
        verify(memberRepository, times(1)).findAllById(rq.getAccountIds());
    }

    @Test(expected = CustomException.class)
    public void addParticipants_NotExistedWorkspace_Success() {
        // given
        final Long workspaceId = 1L;
        List<Long> accountIds = Arrays.asList();
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.create(workspaceId, accountIds);

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())).thenReturn(null);

        // when
        workspaceService.addParticipants(rq);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void addParticipants_UnauthorizedMember_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long workspaceId = 1L;
        List<Long> accountIds = Arrays.asList(new Long[]{new Long(555)});
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.create(workspaceId, accountIds);

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())).thenReturn(workspace);

        // when
        workspaceService.addParticipants(rq);

        // then
        fail("참가자 등록 권한 체크시 예외가 발생해야 합니다.");
    }

    @Test
    public void findById_ValidInput_Success() {
        // given
        final String testWorkspaceName = "test-workspace-name";
        Workspace workspace = Workspace.create(testWorkspaceName, Participant.create(member));
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspaceId);

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);

        verify(workspaceRepository).findById(workspaceId);
        verify(workspaceRepository, times(1)).findById(workspaceId);
    }

    @Test(expected = CustomException.class)
    public void findById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findAllByMemberId_ValidInput_Success() {
        // given
        final Long memberId = 1L;

        // mocking
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(workspaceRepository.findAllByMemberIdFetchJoinParticipant(memberId)).thenReturn(anyList());

        // when
        List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(memberId);

        // then
        verify(memberRepository).existsById(memberId);
        verify(memberRepository, times(1)).existsById(memberId);

        verify(workspaceRepository).findAllByMemberIdFetchJoinParticipant(memberId);
        verify(workspaceRepository, times(1)).findAllByMemberIdFetchJoinParticipant(memberId);


    }

    @Test(expected = CustomException.class)
    public void findAllByMemberId_NotExistedMember_ThrowCustomException() {
        // given
        final Long memberId = 1L;

        // mocking
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // when
        List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(memberId);

        // then
        fail("회원 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findMembersById_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(true);
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);

        // when
        List<MemberResponseDto> result = workspaceService.findMembersById(workspaceId);

        // then
        assertThat(result.get(0).getAccountId()).isEqualTo(member.getAccountId());

        verify(workspaceRepository).existsById(workspaceId);
        verify(workspaceRepository, times(1)).existsById(workspaceId);

        verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(workspaceId);
        verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(workspaceId);
    }

    @Test(expected = CustomException.class)
    public void findMembersById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(false);

        // when
        List<MemberResponseDto> result = workspaceService.findMembersById(workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void deleteById_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);
        doNothing().when(workspaceRepository).delete(any(Workspace.class));

        // when
        workspaceService.deleteById(workspaceId);

        // then
        verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(workspaceId);
        verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(workspaceId);

        verify(workspaceRepository).delete(any(Workspace.class));
        verify(workspaceRepository, times(1)).delete(any(Workspace.class));
    }

    @Test(expected = CustomException.class)
    public void deleteById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(null);

        // when
        workspaceService.deleteById(workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void deleteParticipantByMemberId_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long memberId = 1L;
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(workspaceId);
        verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(workspaceId);
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long memberId = 1L;
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(null);

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_NotExistedParticipant_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        workspace.getParticipantGroup().removeParticipant(member.getId());

        final Long memberId = 1L;
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("입력 받은 회원이 작업 공간에 참가된 회원인지 확인시 예외가 발생해야 합니다.");
    }

}