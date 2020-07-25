package com.code4ro.legalconsultation.controller;

import com.amazonaws.util.json.Jackson;
import com.code4ro.legalconsultation.service.api.StorageApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final StorageApi storageApi;

    @ApiOperation(value = "Add a new file that will be attached one document")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createFile(
            @ApiParam(value = "The file containing that will content the document content") @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(Jackson.toJsonString(storageApi.storeFile(file)));
    }

    @ApiOperation(value = "Delete a file that is attached to a document")
    @DeleteMapping("")
    public void deleteFile(
            @ApiParam(value = "The filePath that will be deleted") @RequestParam("filePath") String filePath) {
        storageApi.deleteFile(filePath);
    }
}
