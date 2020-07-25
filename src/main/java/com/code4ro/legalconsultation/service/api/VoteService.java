package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.dto.VoteDto;

import java.util.Set;
import java.util.UUID;

public interface VoteService {
    VoteDto vote(VoteDto voteDto);

    Set<VoteDto> getAnonymousVotesForComment(UUID commentId);
}
