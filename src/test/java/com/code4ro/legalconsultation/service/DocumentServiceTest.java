package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.service.impl.DocumentConsolidatedService;
import com.code4ro.legalconsultation.service.impl.DocumentMetadataService;
import com.code4ro.legalconsultation.service.impl.DocumentServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @Mock
    private DocumentConsolidatedService documentConsolidatedService;

    @Mock
    private DocumentMetadataService documentMetadataService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    public void getDocument(){
        final UUID uuid = UUID.randomUUID();
        when(documentMetadataService.fetchOne(any(UUID.class))).thenReturn(Optional.of(new DocumentMetadata()));

        documentService.fetchOne(uuid);

        verify(documentMetadataService).fetchOne(uuid);
    }

    @Test
    public void getDocumentConsolidated(){
        final UUID uuid = UUID.randomUUID();
        when(documentConsolidatedService.getOne(any(UUID.class))).thenReturn(new DocumentConsolidatedDto());

        documentService.fetchOneConsolidated(uuid);

        verify(documentConsolidatedService).getOne(uuid);
    }

    @Test
    public void getAllDocuments(){
        documentService.fetchAll(Pageable.unpaged());

        verify(documentMetadataService).fetchAll(Pageable.unpaged());
    }

    @Test
    public void deleteDocument(){
        final UUID uuid = UUID.randomUUID();

        documentService.deleteById(uuid);

        verify(documentConsolidatedService).deleteById(uuid);
    }

}
