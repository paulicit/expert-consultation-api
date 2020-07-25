package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.dto.*;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentExportFormat;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public interface DocumentService {
    //return all metadata for documents
    Page<DocumentMetadata> fetchAll(Pageable pageable);

    //return document metadata for a single document
    DocumentMetadataDto fetchOne(final UUID id);

    //return the entire consolidated form of the document,
    //that contains the metadata and the breakdown into units
    DocumentConsolidatedDto fetchConsolidatedByMetadataId(final UUID id);

    /**
     * @param id of one of the document nodes contained in the requested document
     * @return the entire consolidated form of the document,
     * that contains the metadata and the breakdown into units
     */
    DocumentConsolidatedDto fetchConsolidatedByDocumentNodeId(final UUID id);

    //create a single document, including metadata and breakdown
    DocumentConsolidated create(final DocumentViewDto document);

    //update a single document
    DocumentConsolidated update(final UUID id,
                                final DocumentViewDto document);

    //delete a document based on id
    void deleteById(final UUID id);

    void assignUsers(final UUID id, final Set<UUID> userIds);

    List<UserDto> getAssignedUsers(final UUID id);

    PdfHandleDto addPdf(final UUID id, final String state, final MultipartFile file);

    byte[] export(final UUID id, final DocumentExportFormat type);
}
