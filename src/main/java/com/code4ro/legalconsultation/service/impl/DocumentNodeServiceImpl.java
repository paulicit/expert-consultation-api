package com.code4ro.legalconsultation.service.impl;

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
    public DocumentNode update(DocumentNode documentNode) {
        log.info("Update DocumentNode: {}", documentNode);
        return documentNodeRepository.save(documentNode);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Delete DocumentNode by id: {}", id);
        documentNodeRepository.deleteById(id);
    }
}
