package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.converters.DocumentNodeMapper;
import com.code4ro.legalconsultation.model.dto.documentnode.DocumentNodeCreateDto;
import com.code4ro.legalconsultation.model.dto.documentnode.DocumentNodeDto;
import com.code4ro.legalconsultation.model.dto.documentnode.DocumentNodeSimpleDto;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/document-nodes")
@RequiredArgsConstructor
public class DocumentNodeController {

    private final DocumentNodeService documentNodeService;
    private final DocumentNodeMapper mapper;

    @ApiOperation(value = "Return document node based on id",
            response = DocumentNodeDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentNodeDto getDocumentNodeById(@ApiParam("Id of the document node object being requested") @PathVariable UUID id) {
        DocumentNode documentNode = documentNodeService.findById(id);

        return mapper.map(documentNode);
    }

    @ApiOperation(value = "Create a new document node in the platform",
            response = UUID.class,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> create(
            @Valid @RequestBody DocumentNodeCreateDto documentNodeCreateDto) {

        DocumentNode inputDocumentNode = mapper.map(documentNodeCreateDto);
        if (documentNodeCreateDto.getParentId() != null) {
            DocumentNode parent = documentNodeService.findById(documentNodeCreateDto.getParentId());
            inputDocumentNode.setParent(parent);
        }
        DocumentNode documentNode = documentNodeService.create(inputDocumentNode);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documentNode.getId());
    }

    @ApiOperation(value = "Modify a saved document node in the platform",
            response = UUID.class,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentNodeSimpleDto> modifyDocument(@ApiParam(value = "Id of the document node being modified") @PathVariable("id") UUID id,
                                                                @Valid @RequestBody DocumentNodeSimpleDto documentNodeDto) {
        DocumentNode documentNode = documentNodeService.update(documentNodeDto);
        return ResponseEntity.ok(mapper.mapToSimpleDto(documentNode));
    }

    @ApiOperation(value = "Delete document node based on id")
    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> deleteDocument(@ApiParam("Id of the document node being deleted") @PathVariable UUID id) {
        DocumentNode rootNode = documentNodeService.findRootNodeForId(id);
        documentNodeService.deleteById(id);
        return ResponseEntity.ok(rootNode.getId());
    }
}
