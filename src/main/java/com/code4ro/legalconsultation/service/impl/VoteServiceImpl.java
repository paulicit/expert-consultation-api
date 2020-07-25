package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.config.security.CurrentUserService;
import com.code4ro.legalconsultation.model.dto.VoteDto;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.Comment;
import com.code4ro.legalconsultation.model.persistence.Vote;
import com.code4ro.legalconsultation.repository.VoteRepository;
import com.code4ro.legalconsultation.service.api.CommentService;
import com.code4ro.legalconsultation.service.api.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

@Service
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final CurrentUserService currentUserService;
    private final CommentService commentService;

    public VoteServiceImpl(VoteRepository voteRepository,
                           CurrentUserService currentUserService,
                           CommentService commentService) {
        this.voteRepository = voteRepository;
        this.currentUserService = currentUserService;
        this.commentService = commentService;
    }

    @Transactional(readOnly = true)
    public Set<VoteDto> getAnonymousVotesForComment(UUID commentId) {
        if (commentId == null) {
            return new HashSet<>();
        }

        List<Vote> documentVotes = this.voteRepository.findByCommentId(commentId);
        if (CollectionUtils.isEmpty(documentVotes)) {
            return new HashSet<>();
        }

        return documentVotes
                .parallelStream()
                .map(vote -> new VoteDto(vote.getComment().getId(), vote.getVote()))
                .collect(toSet());
    }

    @Transactional
    @Override
    public VoteDto vote(VoteDto voteDto) {
        if (voteDto == null || voteDto.getCommentId() == null || voteDto.getVote() == null) {
            return null;
        }

        final ApplicationUser currentUser = currentUserService.getCurrentUser();
        final Comment comment = commentService.findById(voteDto.getCommentId());

//        TODO After adding Document Voting Period,
//         add check for voting only if current time is during that voting period

        Vote voteEntity = voteRepository.findByCommentIdAndOwnerId(voteDto.getCommentId(), currentUser.getId());
        if (voteEntity == null) {
            voteEntity = new Vote(comment, currentUser);
        }
        voteEntity.setVote(voteDto.getVote());
        voteEntity.setLastEditDateTime(new Date());

        Vote savedEntity = voteRepository.save(voteEntity);
        voteDto.setId(savedEntity.getId());
        return voteDto;
    }

}
