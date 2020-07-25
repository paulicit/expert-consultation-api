package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.model.DocumentUserAssignmentDto;
import com.code4ro.legalconsultation.model.dto.*;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentExportFormat;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.service.api.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @ApiOperation(value = "Return document metadata for all documents in the platform",
            response = PageDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("")
    public ResponseEntity<PageDto<DocumentMetadata>> getAllDocuments(@ApiParam("Page object information being requested") Pageable pageable) {
        Page<DocumentMetadata> documents = documentService.fetchAll(pageable);

        return ResponseEntity.ok(new PageDto<>(documents));
    }

    @ApiOperation(value = "Return document metadata for a single document in the platform based on id",
            response = DocumentMetadataDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}")
    public ResponseEntity<DocumentMetadataDto> getDocumentMetadataById(@ApiParam("Id of the document object being requested") @PathVariable UUID id) {
        return ResponseEntity.ok(documentService.fetchOne(id));
    }

    @ApiOperation(value = "Return metadata and content for a single document in the platform based on metadata id",
            response = DocumentConsolidatedDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}/consolidated")
    public ResponseEntity<DocumentConsolidatedDto> getDocumentConsolidatedById(@ApiParam("Id of the document metadata object being requested") @PathVariable UUID id) {
        return ResponseEntity.ok(documentService.fetchConsolidatedByMetadataId(id));
    }

    @GetMapping("/{id}/node")
    public ResponseEntity<DocumentConsolidatedDto> getDocumentConsolidatedByDocumentNodeId(
            @ApiParam("Id of on of the nodes included in the document being requested") @PathVariable UUID id) {
        return ResponseEntity.ok(documentService.fetchConsolidatedByDocumentNodeId(id));
    }

    @ApiOperation(value = "Delete metadata and contents for a single document in the platform based on id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@ApiParam("Id of the document object being deleted") @PathVariable UUID id) {
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Create a new document in the platform",
            response = UUID.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("")
    public ResponseEntity<UUID> createDocument(
            @Valid @RequestBody DocumentViewDto documentViewDto) {
        // TODO: decide the configuration for each document will be provided,
        // currently the service will initialise it with default true values
        DocumentConsolidated consolidated = documentService.create(documentViewDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consolidated.getDocumentMetadata().getId());
    }

    @ApiOperation(value = "Modify a saved document in the platform",
            response = UUID.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/{id}")
    public ResponseEntity<UUID> modifyDocument(@ApiParam(value = "Id of the document being modified") @PathVariable("id") UUID id,
                                               @Valid @RequestBody DocumentViewDto documentViewDto) {
        DocumentConsolidated consolidated = documentService.update(id, documentViewDto);
        return ResponseEntity.ok(consolidated.getId());
    }

    @ApiOperation("Assign users to a document")
    @PostMapping("/{id}/users")
    public ResponseEntity<Void> assignUsers(@ApiParam(value = "Id of the document being modified") @PathVariable("id") UUID id,
                                            @Valid @RequestBody DocumentUserAssignmentDto documentUserAssignmentDto) {
        documentService.assignUsers(id, documentUserAssignmentDto.getUserIds());
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get assigned users of a document")
    @GetMapping("{id}/users")
    public ResponseEntity<List<UserDto>> getAssignedUsers(@ApiParam(value = "Id of the document") @PathVariable("id") UUID id) {
        final List<UserDto> assignedUsers = documentService.getAssignedUsers(id);
        return ResponseEntity.ok(assignedUsers);
    }

    @ApiOperation(value = "Upload a pdf to this document",
            consumes = MediaType.APPLICATION_PDF_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = PdfHandleDto.class)
    @PostMapping("/{id}/pdf")
    public ResponseEntity<PdfHandleDto> uploadPdf(@ApiParam(value = "Id of the document") @PathVariable UUID id,
                                                  @ApiParam(value = "State of the pdf document") @RequestParam String state,
                                                  @ApiParam(value = "The pdf document") @RequestBody MultipartFile file) {
        return ResponseEntity.ok(documentService.addPdf(id, state, file));
    }

    @ApiOperation(value = "Generate a PDF file from this document",
            consumes = MediaType.APPLICATION_PDF_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}/export")
    public ResponseEntity<ByteArrayResource> generate(@PathVariable UUID id,
                                                      @RequestParam("type") DocumentExportFormat type) {
        final byte[] pdfContent = documentService.export(id, type);
        return ResponseEntity.ok()
                .contentLength(pdfContent.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdfContent));
    }
}
