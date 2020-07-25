package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.model.dto.documentnode.DocumentNodeSimpleDto;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.repository.DocumentNodeRepository;
import com.code4ro.legalconsultation.service.api.DocumentNodeService;
import com.code4ro.legalconsultation.service.impl.pdf.parser.DocumentParser;
import com.code4ro.legalconsultation.service.impl.pdf.parser.DocumentParsingMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
@Slf4j
public class DocumentNodeServiceImpl implements DocumentNodeService {

    private final DocumentNodeRepository documentNodeRepository;
    private final DocumentParser documentParser;

    @Autowired
    public DocumentNodeServiceImpl(final DocumentNodeRepository documentNodeRepository,
                                   final DocumentParser documentParser) {
        this.documentNodeRepository = documentNodeRepository;
        this.documentParser = documentParser;
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentNode findById(final UUID id) {
        return documentNodeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public DocumentNode findRootNodeForId(UUID id) {
        DocumentNode root = findById(id);
        while (root.getParent() != null) {
            root = root.getParent();
        }

        return root;
    }

    @Transactional
    @Override
    public DocumentNode parse(final String pdfContent) {
        final String[] lines = pdfContent.split("\\r\\n|\\n");
        final DocumentParsingMetadata metadata = new DocumentParsingMetadata(lines.length);

        return documentParser.parse(lines, metadata);
    }

    @Override
    public DocumentNode create(DocumentNode documentNode) {
        log.info("Save DocumentNode: {}", documentNode);
        return documentNodeRepository.save(documentNode);
    }

    @Override
    public DocumentNode update(DocumentNodeSimpleDto documentNode) {
        log.info("Update DocumentNode: {}", documentNode);
        final DocumentNode exitingDocumentNode = documentNodeRepository.findById(documentNode.getId())
                .orElseThrow(EntityNotFoundException::new);
        exitingDocumentNode.setTitle(documentNode.getTitle());
        exitingDocumentNode.setIdentifier(documentNode.getIdentifier());
        exitingDocumentNode.setContent(documentNode.getContent());
        return documentNodeRepository.save(exitingDocumentNode);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        DocumentNode documentNode = documentNodeRepository.getOne(id);
        DocumentNode parentNode = documentNode.getParent();
        if (parentNode != null) {
            parentNode.getChildren().remove(documentNode);
            documentNodeRepository.save(parentNode);
        }

        documentNodeRepository.deleteById(id);
    }
}
