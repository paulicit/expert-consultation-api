package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.model.dto.VoteDto;
import com.code4ro.legalconsultation.service.api.VoteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @ApiOperation(value = "Return anonymous votes for a comment",
            response = Set.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{commentId}")
    public ResponseEntity<Set<VoteDto>> getAnonymousVotesFor(
            @ApiParam(value = "Comment Id for vote") @PathVariable("commentId") UUID commentId) {
        return ok(voteService.getAnonymousVotesForComment(commentId));
    }

    @ApiOperation(value = "Create vote for a comment",
            response = VoteDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/")
    public ResponseEntity<VoteDto> updateVote(@Valid @RequestBody VoteDto voteDtoReq) {
        return ok(voteService.vote(voteDtoReq));
    }

    @ApiOperation(value = "Update vote for comment",
            response = VoteDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/")
    public ResponseEntity<VoteDto> saveVote(@Valid @RequestBody VoteDto voteDtoReq) {
        return ok(voteService.vote(voteDtoReq));
    }
}
