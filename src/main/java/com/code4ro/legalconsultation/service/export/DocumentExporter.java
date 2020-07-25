package com.code4ro.legalconsultation.service.export;

import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;

public interface DocumentExporter {
    byte[] export(DocumentConsolidated document);
}
