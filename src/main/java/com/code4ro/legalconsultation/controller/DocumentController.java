package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.model.DocumentUserAssignmentDto;
import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.dto.DocumentMetadataDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.service.api.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @ApiOperation(value = "Return document metadata for all documents in the platform",
            response = List.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("")
    public ResponseEntity<Page<DocumentMetadata>> getAllDocuments(@ApiParam("Page object information being requested") Pageable pageable) {
        Page<DocumentMetadata> documents = documentService.fetchAll(pageable);

        return new ResponseEntity<>(documents, HttpStatus.OK);
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
                .body(consolidated.getId());
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
}
