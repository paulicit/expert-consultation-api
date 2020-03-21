package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConfiguration;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import com.code4ro.legalconsultation.service.api.DocumentService;
import com.code4ro.legalconsultation.service.api.PDFService;
import com.code4ro.legalconsultation.service.api.StorageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentConsolidatedService documentConsolidatedService;
    private final DocumentMetadataService documentMetadataService;
    private final PDFService pdfService;
    private final DocumentNodeService documentNodeService;
    private final StorageApi storageApi;

    @Autowired
    public DocumentServiceImpl(final DocumentConsolidatedService documentConsolidatedService,
                               final DocumentMetadataService documentMetadataService,
                               final PDFService pdfService,
                               final DocumentNodeService documentNodeService,
                               final StorageApi storageApi) {
        this.documentConsolidatedService = documentConsolidatedService;
        this.documentMetadataService = documentMetadataService;
        this.pdfService = pdfService;
        this.documentNodeService = documentNodeService;
        this.storageApi = storageApi;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DocumentMetadata> fetchAll(Pageable pageable) {
        return documentMetadataService.fetchAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentMetadata fetchOne(final UUID id) {
        return documentMetadataService.fetchOne(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentConsolidatedDto fetchOneConsolidated(final UUID id) {
        return documentConsolidatedService.getOne(id);
    }

    @Transactional
    @Override
    public DocumentConsolidated create(final DocumentViewDto document) {

        DocumentMetadata metadata = documentMetadataService.build(document);
        metadata.setFilePath(document.getFilePath());
        final String pdfContent = pdfService.readAsString(storageApi.loadFile(document.getFilePath()));
        final DocumentNode documentNode = documentNodeService.parse(pdfContent);
        final DocumentConfiguration documentConfiguration = new DocumentConfiguration(true, true);

        return documentConsolidatedService.saveOne(new DocumentConsolidated(metadata, documentNode, documentConfiguration));
    }

    @Transactional
    @Override
    public DocumentConsolidated update(final UUID id,
                                       final DocumentViewDto document) {
        final DocumentConsolidated consolidated = documentConsolidatedService.getEntity(id);

        // TODO delete current file from storage and the document node

        //update the metadata
        DocumentMetadata metadata = documentMetadataService.build(document);
        metadata.setFilePath(document.getFilePath());
        metadata.setId(consolidated.getDocumentMetadata().getId());

        final String pdfContent = pdfService.readAsString(storageApi.loadFile(document.getFilePath()));
        final DocumentNode documentNode = documentNodeService.parse(pdfContent);

        consolidated.setDocumentMetadata(metadata);
        consolidated.setDocumentNode(documentNode);
        return documentConsolidatedService.saveOne(consolidated);
    }

    @Transactional
    @Override
    public void deleteById(final UUID id) {
        documentConsolidatedService.getEntity(id);
        documentConsolidatedService.deleteById(id);
    }
}
