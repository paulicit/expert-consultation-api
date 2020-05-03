package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.converters.DocumentMetadataMapper;
import com.code4ro.legalconsultation.model.dto.DocumentMetadataDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.repository.DocumentMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class DocumentMetadataService {

    private final DocumentMetadataRepository documentMetadataRepository;
    private final DocumentMetadataMapper mapperService;

    @Autowired
    public DocumentMetadataService(DocumentMetadataRepository documentMetadataRepository,
                                   DocumentMetadataMapper mapperService) {
        this.documentMetadataRepository = documentMetadataRepository;
        this.mapperService = mapperService;
    }

    public DocumentMetadata save(final DocumentMetadata documentMetadata) {
        return documentMetadataRepository.save(documentMetadata);
    }

    public Page<DocumentMetadata> fetchAll(Pageable pageable) {
        return documentMetadataRepository.findAll(pageable);
    }

    public DocumentMetadataDto fetchOne(final UUID id) {
        DocumentMetadata documentMetadata = documentMetadataRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return mapperService.map(documentMetadata);
    }

    public DocumentMetadata build(final DocumentViewDto document) {
        return mapperService.map(document);
    }

    public DocumentMetadata create(final DocumentViewDto document) {
        return documentMetadataRepository.save(mapperService.map(document));
    }

    public DocumentMetadata update(final String id, final DocumentViewDto document) {
        DocumentMetadata metadata = mapperService.map(document);
        metadata.setId(UUID.fromString(id));
        return documentMetadataRepository.save(metadata);
    }

    public void deleteById(final String id) {
        documentMetadataRepository.deleteById(UUID.fromString(id));
    }
}
