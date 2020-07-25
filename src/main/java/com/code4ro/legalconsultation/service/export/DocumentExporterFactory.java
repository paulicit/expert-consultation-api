package com.code4ro.legalconsultation.service.export;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.persistence.DocumentExportFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DocumentExporterFactory {
    private final PDFExporter pdfExporter;

    @Autowired
    public DocumentExporterFactory(final PDFExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    public DocumentExporter getExporter(final DocumentExportFormat type) {
        if (type.equals(DocumentExportFormat.PDF)) {
            return pdfExporter;
        }

        throw LegalValidationException.builder()
                .i18nKey("document.export.format.invalid")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }
}
