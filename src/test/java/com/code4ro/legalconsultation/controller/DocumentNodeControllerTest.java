package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.converters.DocumentNodeMapper;
import com.code4ro.legalconsultation.converters.DocumentNodeMapperImpl;
import com.code4ro.legalconsultation.model.dto.documentnode.DocumentNodeCreateDto;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentNodeControllerTest {

    @InjectMocks
    private DocumentNodeController documentNodeController;

    @Mock
    private DocumentNodeService documentNodeService;

    @Mock
    private DocumentNodeMapper mapper;
    private DocumentNodeMapperImpl mapperImpl;

    @Captor
    private ArgumentCaptor<DocumentNode> documentNodeCaptor;

    @Before
    public void setUp() {
        mapperImpl = new DocumentNodeMapperImpl();
        when(mapper.map(any(DocumentNodeCreateDto.class))).thenAnswer(i -> {
            DocumentNodeCreateDto documentNodeDto = i.getArgument(0);
            return mapperImpl.map(documentNodeDto);
        });
    }

    @Test
    public void shouldCreateDocumentNode() {
        //given
        String title = "mockTitle";
        DocumentNodeCreateDto documentNodeCreateDto = new DocumentNodeCreateDto();
        documentNodeCreateDto.setTitle(title);

        when(documentNodeService.create(any())).thenAnswer(i -> i.getArgument(0));

        //when
        documentNodeController.create(documentNodeCreateDto);

        //then
        verify(documentNodeService).create(documentNodeCaptor.capture());
        DocumentNode documentNodeToCreate = documentNodeCaptor.getValue();

        assertEquals("parentDocumentNode is different", title, documentNodeToCreate.getTitle());
    }

    @Test
    public void shouldCreateDocumentNodeWithParent() {
        //given
        UUID parentId = UUID.randomUUID();
        DocumentNodeCreateDto documentNodeCreateDto = new DocumentNodeCreateDto();
        documentNodeCreateDto.setParentId(parentId);

        DocumentNode parentDocumentNode = new DocumentNode();
        parentDocumentNode.setId(parentId);
        when(documentNodeService.findById(parentId)).thenReturn(parentDocumentNode);
        when(documentNodeService.create(any())).thenAnswer(i -> i.getArgument(0));

        //when
        documentNodeController.create(documentNodeCreateDto);

        //then
        verify(documentNodeService).create(documentNodeCaptor.capture());
        DocumentNode documentNodeToCreate = documentNodeCaptor.getValue();

        assertEquals("parentDocumentNode is different", parentDocumentNode, documentNodeToCreate.getParent());
    }
}
