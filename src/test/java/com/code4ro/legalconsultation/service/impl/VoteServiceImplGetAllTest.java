package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.config.security.CurrentUserService;
import com.code4ro.legalconsultation.model.dto.VoteDto;
import com.code4ro.legalconsultation.model.persistence.*;
import com.code4ro.legalconsultation.repository.VoteRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.code4ro.legalconsultation.factory.RandomObjectFiller.createAndFill;
import static com.code4ro.legalconsultation.model.persistence.UserRole.ADMIN;
import static com.code4ro.legalconsultation.model.persistence.VoteType.DOWN;
import static com.code4ro.legalconsultation.model.persistence.VoteType.UP;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class VoteServiceImplGetAllTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private VoteServiceImpl voteService;

    private ApplicationUser currentUser = createAndFill(ApplicationUser.class);
    private UUID uuidComment = randomUUID();

    @Before
    public void before() {
        currentUser.getUser().setRole(ADMIN);
        currentUser.setId(randomUUID());
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void getAnonymousVotes_expectedSingleVoteUp() {
        List<Vote> voteEntities = new ArrayList<>();
        voteEntities.add(createVoteEntity(new ApplicationUser(), UP, uuidComment));
        when(this.voteRepository.findByCommentId(uuidComment)).thenReturn(voteEntities);

        Set<VoteDto> actualVotes = voteService.getAnonymousVotesForComment(uuidComment);

        assertFalse(actualVotes.isEmpty());
        assertEquals(1, actualVotes.size());

        VoteDto voteDto = actualVotes.iterator().next();
        assertEquals(UP, voteDto.getVote());
        assertEquals(uuidComment, voteDto.getCommentId());
    }

    @Test
    public void getAnonymousVotes_expectedSingleVoteDown() {
        List<Vote> voteEntities = new ArrayList<>();
        voteEntities.add(createVoteEntity(new ApplicationUser(), DOWN, uuidComment));
        when(this.voteRepository.findByCommentId(uuidComment)).thenReturn(voteEntities);

        Set<VoteDto> actualVotes = voteService.getAnonymousVotesForComment(uuidComment);

        assertFalse(actualVotes.isEmpty());
        assertEquals(1, actualVotes.size());

        VoteDto voteDto = actualVotes.iterator().next();
        assertEquals(DOWN, voteDto.getVote());
        assertEquals(uuidComment, voteDto.getCommentId());
    }

    @Test
    public void getAnonymousVotes_expectedSingleVoteOfCurrentUserUp() {
        List<Vote> voteEntities = new ArrayList<>();
        voteEntities.add(createVoteEntity(currentUser, UP, uuidComment));
        when(this.voteRepository.findByCommentId(uuidComment)).thenReturn(voteEntities);

        Set<VoteDto> actualVotes = voteService.getAnonymousVotesForComment(uuidComment);

        assertFalse(actualVotes.isEmpty());
        assertEquals(1, actualVotes.size());

        VoteDto voteDto = actualVotes.iterator().next();
        assertEquals(UP, voteDto.getVote());
        assertEquals(uuidComment, voteDto.getCommentId());
    }

    private Vote createVoteEntity(ApplicationUser appUser, VoteType vote, UUID commentId) {
        Vote entity = new Vote();
        entity.setVote(vote);
        Comment comment = new Comment();
        comment.setId(commentId);
        entity.setComment(comment);
        entity.setOwner(appUser);
        return entity;
    }

    private DocumentNode createDocumentNode(UUID uuidDocNode1) {
        DocumentNode documentNode = new DocumentNode();
        documentNode.setId(uuidDocNode1);
        return documentNode;
    }
}
