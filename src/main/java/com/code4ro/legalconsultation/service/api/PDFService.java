package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.PdfHandle;
import org.springframework.web.multipart.MultipartFile;

public interface PDFService {
    String readAsString(byte[] file);

    PdfHandle createPdf(DocumentConsolidated documentConsolidatedId, String state, MultipartFile file);
}
