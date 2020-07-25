package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.config.security.CurrentUserService;
import com.code4ro.legalconsultation.converters.CommentMapper;
import com.code4ro.legalconsultation.model.dto.CommentDetailDto;
import com.code4ro.legalconsultation.model.dto.CommentDto;
import com.code4ro.legalconsultation.model.persistence.*;
import com.code4ro.legalconsultation.repository.CommentRepository;
import com.code4ro.legalconsultation.service.api.CommentService;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CurrentUserService currentUserService;
    private final DocumentNodeService documentNodeService;
    private final CommentMapper mapperService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              CurrentUserService currentUserService,
                              DocumentNodeService documentNodeService,
                              CommentMapper mapperService) {
        this.commentRepository = commentRepository;
        this.currentUserService = currentUserService;
        this.documentNodeService = documentNodeService;
        this.mapperService = mapperService;
    }

    @Transactional
    @Override
    public CommentDetailDto update(UUID nodeId, final UUID id, final CommentDto commentDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        checkIfAuthorized(comment);

        comment.setText(commentDto.getText());
        comment = commentRepository.save(comment);

        return mapperService.mapToCommentDetailDto(comment);
    }

    @Transactional
    @Override
    public CommentDetailDto create(UUID nodeId, final CommentDto commentDto) {
        final DocumentNode node = documentNodeService.findById(nodeId);

        final ApplicationUser currentUser = currentUserService.getCurrentUser();

        Comment comment = mapperService.map(commentDto);
        comment.setDocumentNode(node);
        comment.setOwner(currentUser);
        comment.setLastEditDateTime(new Date());
        comment = commentRepository.save(comment);

        return mapperService.mapToCommentDetailDto(comment);
    }

    @Transactional
    @Override
    public CommentDetailDto createReply(UUID parentId, CommentDto commentDto) {
        Comment parent = commentRepository.findById(parentId).orElseThrow(EntityNotFoundException::new);
        ApplicationUser currentUser = currentUserService.getCurrentUser();

        Comment reply = mapperService.map(commentDto);
        reply.setParent(parent);
        reply.setOwner(currentUser);
        reply.setLastEditDateTime(new Date());

        reply = commentRepository.save(reply);

        return mapperService.mapToCommentDetailDto(reply);
    }

    @Transactional
    @Override
    public void delete(final UUID id) {
        final Comment comment = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        checkIfAuthorized(comment);

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Comment> findAll(final UUID documentNodeId, final Pageable pageable) {
        return commentRepository.findByDocumentNodeId(documentNodeId, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Comment> findAllReplies(UUID parentId, Pageable pageable) {
        return commentRepository.findByParentId(parentId, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public BigInteger count(UUID nodeId) {
        return commentRepository.countByDocumentNodeId(nodeId);
    }

    @Transactional
    @Override
    public CommentDto setStatus(UUID commentId, CommentStatus status) {
        final Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (comment.getStatus() != null) {
            throw LegalValidationException.builder()
                    .i18nKey("comment.Status.already.set")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }
        comment.setStatus(status);
        commentRepository.save(comment);
        return mapperService.map(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(UUID id) {
        return this.commentRepository.getOne(id);
    }

    private void checkIfAuthorized(Comment comment) {
        final ApplicationUser owner = comment.getOwner();
        final ApplicationUser currentUser = currentUserService.getCurrentUser();
        if (currentUser.getUser().getRole() != UserRole.ADMIN && !Objects.equals(currentUser.getId(), owner.getId())) {
            throw LegalValidationException.builder()
                    .i18nKey("comment.Unauthorized.user")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
