package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.config.security.CurrentUserService;
import com.code4ro.legalconsultation.factory.CommentFactory;
import com.code4ro.legalconsultation.factory.RandomObjectFiller;
import com.code4ro.legalconsultation.model.dto.VoteDto;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.Comment;
import com.code4ro.legalconsultation.model.persistence.Vote;
import com.code4ro.legalconsultation.repository.VoteRepository;
import com.code4ro.legalconsultation.service.api.CommentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static com.code4ro.legalconsultation.model.persistence.UserRole.ADMIN;
import static com.code4ro.legalconsultation.model.persistence.VoteType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VoteServiceImplSaveOrUpdateTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private VoteServiceImpl voteService;

    @Captor
    private ArgumentCaptor<Vote> voteArgumentCaptor;

    private final CommentFactory commentFactory = new CommentFactory();
    private ApplicationUser currentUser;
    private Comment comment;
    private UUID id;
    private Vote alreadyExistingVote;

    @Before
    public void before() {
        currentUser = RandomObjectFiller.createAndFill(ApplicationUser.class);
        currentUser.getUser().setRole(ADMIN);

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        comment = commentFactory.createEntity();
        when(commentService.findById(any())).thenReturn(comment);
        id = UUID.randomUUID();

        alreadyExistingVote = new Vote();
        alreadyExistingVote.setOwner(currentUser);
        alreadyExistingVote.setComment(comment);
        alreadyExistingVote.setVote(UP);
        alreadyExistingVote.setId(UUID.randomUUID());

        when(voteRepository.save(any())).thenReturn(alreadyExistingVote);
    }


    @Test
    public void voteUpUpdate_expectedVoteSaved() {
        final VoteDto expectedUpVoteDto = new VoteDto();
        expectedUpVoteDto.setVote(UP);
        expectedUpVoteDto.setCommentId(id);
        voteService.vote(expectedUpVoteDto);

        verify(voteRepository).save(voteArgumentCaptor.capture());
        final Vote upVote = voteArgumentCaptor.getValue();
        assertThat(upVote.getOwner()).isEqualTo(currentUser);
        assertThat(upVote.getComment()).isEqualTo(comment);
        assertEquals(expectedUpVoteDto.getVote(), upVote.getVote());
    }

    @Test
    public void voteDownUpdate_expectedVoteSaved() {
        final VoteDto expectedDownVoteDto = new VoteDto();
        expectedDownVoteDto.setVote(DOWN);
        expectedDownVoteDto.setCommentId(id);
        when(voteRepository.findByCommentIdAndOwnerId(id, currentUser.getId())).thenReturn(alreadyExistingVote);
        voteService.vote(expectedDownVoteDto);

        verify(voteRepository).save(voteArgumentCaptor.capture());
        final Vote actualDownVote = voteArgumentCaptor.getValue();

        assertThat(actualDownVote.getOwner()).isEqualTo(currentUser);
        assertThat(actualDownVote.getComment()).isEqualTo(comment);
        assertEquals(expectedDownVoteDto.getVote(), actualDownVote.getVote());
    }

    @Test
    public void voteNullWithNoVoteExisting_expectedNoVoteSaved() {
        final VoteDto nullVoteDto = new VoteDto();
        nullVoteDto.setVote(null);
        nullVoteDto.setCommentId(id);
        Object result = voteService.vote(nullVoteDto);
        assertThat(result).isEqualTo(null);
    }

    @Test
    public void voteAbstainUpdate_expectedVoteSaved() {
        final VoteDto nullVoteDto = new VoteDto();
        nullVoteDto.setVote(ABSTAIN);
        nullVoteDto.setCommentId(id);
        when(voteRepository.findByCommentIdAndOwnerId(id, currentUser.getId())).thenReturn(alreadyExistingVote);
        voteService.vote(nullVoteDto);

        verify(voteRepository).save(voteArgumentCaptor.capture());
        final Vote nullVote = voteArgumentCaptor.getValue();

        assertThat(nullVote.getOwner()).isEqualTo(currentUser);
        assertThat(nullVote.getComment()).isEqualTo(comment);
        assertEquals(ABSTAIN, nullVote.getVote());
    }

    @Test
    public void voteDownSave_expectedNewVoteSaved() {
        final VoteDto expectedDownVoteDto = new VoteDto();
        expectedDownVoteDto.setVote(DOWN);
        expectedDownVoteDto.setCommentId(id);

        when(voteRepository.findByCommentIdAndOwnerId(id, currentUser.getId())).thenReturn(null);
        voteService.vote(expectedDownVoteDto);

        verify(voteRepository).save(voteArgumentCaptor.capture());
        final Vote actualDownVote = voteArgumentCaptor.getValue();

        assertThat(actualDownVote.getOwner()).isEqualTo(currentUser);
        assertThat(actualDownVote.getComment()).isEqualTo(comment);
        assertEquals(expectedDownVoteDto.getVote(), actualDownVote.getVote());
    }

    @Test
    public void voteUpSave_expectedNewVoteSaved() {
        final VoteDto expectedDownVoteDto = new VoteDto();
        expectedDownVoteDto.setVote(UP);
        expectedDownVoteDto.setCommentId(id);

        when(voteRepository.findByCommentIdAndOwnerId(id, currentUser.getId())).thenReturn(null);
        voteService.vote(expectedDownVoteDto);

        verify(voteRepository).save(voteArgumentCaptor.capture());
        final Vote actualDownVote = voteArgumentCaptor.getValue();

        assertThat(actualDownVote.getOwner()).isEqualTo(currentUser);
        assertThat(actualDownVote.getComment()).isEqualTo(comment);
        assertEquals(expectedDownVoteDto.getVote(), actualDownVote.getVote());
    }

    @Test
    public void voteAbstainSave_expectedNewVoteSaved() {
        final VoteDto expectedDownVoteDto = new VoteDto();
        expectedDownVoteDto.setVote(ABSTAIN);
        expectedDownVoteDto.setCommentId(id);

        when(voteRepository.findByCommentIdAndOwnerId(id, currentUser.getId())).thenReturn(null);
        voteService.vote(expectedDownVoteDto);

        verify(voteRepository).save(voteArgumentCaptor.capture());
        final Vote actualDownVote = voteArgumentCaptor.getValue();

        assertThat(actualDownVote.getOwner()).isEqualTo(currentUser);
        assertThat(actualDownVote.getComment()).isEqualTo(comment);
        assertEquals(expectedDownVoteDto.getVote(), actualDownVote.getVote());
    }
}
