package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;

import java.util.UUID;

public interface DocumentNodeService {
    DocumentNode findById(UUID id);

    DocumentNode parse(String pdfContent);

    DocumentNode create(final DocumentNode documentNode);

    DocumentNode update(DocumentNode documentNode);

    void deleteById(UUID id);
}
