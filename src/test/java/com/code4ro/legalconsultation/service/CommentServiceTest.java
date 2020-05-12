package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.config.security.CurrentUserService;
import com.code4ro.legalconsultation.converters.CommentMapper;
import com.code4ro.legalconsultation.model.dto.CommentDto;
import com.code4ro.legalconsultation.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.model.persistence.Comment;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.model.persistence.UserRole;
import com.code4ro.legalconsultation.repository.CommentRepository;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import com.code4ro.legalconsultation.service.impl.CommentServiceImpl;
import com.code4ro.legalconsultation.util.CommentFactory;
import com.code4ro.legalconsultation.util.DocumentNodeFactory;
import com.code4ro.legalconsultation.util.RandomObjectFiller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper mapperService;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private DocumentNodeService documentNodeService;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private final DocumentNodeFactory documentNodeFactory = new DocumentNodeFactory();
    private final CommentFactory commentFactory = new CommentFactory();
    private ApplicationUser currentUser;

    @Before
    public void before() {
        currentUser = RandomObjectFiller.createAndFill(ApplicationUser.class);
        currentUser.getUser().setRole(UserRole.ADMIN);
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void create() {
        final UUID id = UUID.randomUUID();
        final CommentDto commentDto = RandomObjectFiller.createAndFill(CommentDto.class);
        final DocumentNode documentNode = documentNodeFactory.create();
        final Comment comment = new Comment();
        when(mapperService.map(commentDto)).thenReturn(comment);
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(documentNodeService.findById(any())).thenReturn(documentNode);

        commentService.create(id, commentDto);

        verify(commentRepository).save(comment);
        assertThat(comment.getOwner()).isEqualTo(currentUser);
        assertThat(comment.getDocumentNode()).isEqualTo(documentNode);
    }

    @Test
    public void update() {
        final UUID id = UUID.randomUUID();
        final CommentDto commentDto = RandomObjectFiller.createAndFill(CommentDto.class);
        final Comment comment = new Comment();
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.update(UUID.randomUUID(), id, commentDto);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        assertThat(commentArgumentCaptor.getValue().getText()).isEqualTo(commentDto.getText());
    }

    @Test(expected = LegalValidationException.class)
    public void updateUnauthorizedUser() {
        final UUID id = UUID.randomUUID();
        final CommentDto commentDto = new CommentDto();
        currentUser.getUser().setRole(UserRole.CONTRIBUTOR);
        final Comment comment = commentFactory.createEntity();
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.update(UUID.randomUUID(), id, commentDto);
    }

    @Test
    public void delete() {
        final UUID id = UUID.randomUUID();
        final Comment comment = new Comment();
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.delete(id);

        verify(commentRepository).delete(comment);
    }

    @Test(expected = LegalValidationException.class)
    public void deleteUnauthorizedUser() {
        final UUID id = UUID.randomUUID();
        currentUser.getUser().setRole(UserRole.CONTRIBUTOR);
        final Comment comment = commentFactory.createEntity();
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.delete(id);
    }

    @Test
    public void findAll() {
        //given
        final UUID nodeId = UUID.randomUUID();
        Pageable pageable = mock(Pageable.class);
        String text = "mockText";
        Comment comment1 = new Comment();
        comment1.setText(text);

        when(commentRepository.findByDocumentNodeId(nodeId, pageable)).thenReturn(new PageImpl<>(List.of(comment1)));

        //when
        Page<Comment> all = commentService.findAll(nodeId, pageable);

        //then
        assertEquals("response size should be 1", all.getContent().size(), 1L);
        assertEquals("text is different", all.getContent().get(0).getText(), text);
    }

    @Test
    public void findAllReplies() {
        //given
        UUID parentId = UUID.randomUUID();
        Pageable pageable = mock(Pageable.class);
        String text = "mockText";
        Comment comment1 = new Comment();
        comment1.setText(text);

        when(commentRepository.findByParentId(parentId, pageable)).thenReturn(new PageImpl<>(List.of(comment1)));

        //when
        Page<Comment> all = commentService.findAllReplies(parentId, pageable);

        //then
        assertEquals("response size should be 1", all.getContent().size(), 1L);
        assertEquals("text is different", all.getContent().get(0).getText(), text);
    }

    @Test
    public void createReply() {
        UUID parentId = UUID.randomUUID();
        CommentDto commentDto = RandomObjectFiller.createAndFill(CommentDto.class);
        Comment parent = commentFactory.createEntity();
        Comment reply = new Comment();

        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(mapperService.map(commentDto)).thenReturn(reply);
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        commentService.createReply(parentId, commentDto);

        verify(commentRepository).save(reply);

        assertThat(reply.getOwner()).isEqualTo(currentUser);
        assertThat(reply.getParent()).isEqualTo(parent);
        assertThat(reply.getDocumentNode()).isNull();
    }

}
