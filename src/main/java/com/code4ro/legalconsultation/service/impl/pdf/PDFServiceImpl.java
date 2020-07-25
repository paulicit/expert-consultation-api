package com.code4ro.legalconsultation.service.impl.pdf;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.converters.PdfHandleMapper;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.PdfHandle;
import com.code4ro.legalconsultation.repository.PdfHandleRepository;
import com.code4ro.legalconsultation.service.api.PDFService;
import com.code4ro.legalconsultation.service.api.StorageApi;
import com.code4ro.legalconsultation.service.impl.pdf.reader.BasicOARPdfReader;
import com.code4ro.legalconsultation.service.impl.pdf.reader.PDFReader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Service
@Slf4j
public class PDFServiceImpl implements PDFService {

    private final BasicOARPdfReader basicOARPdfReader;

    private final StorageApi storageApi;
    private final PdfHandleMapper pdfHandleMapper;
    private final PdfHandleRepository pdfHandleRepository;

    @Autowired
    public PDFServiceImpl(final BasicOARPdfReader basicOARPdfReader,
                          final StorageApi storageApi,
                          final PdfHandleMapper pdfHandleMapper,
                          final PdfHandleRepository pdfHandleRepository) {
        this.basicOARPdfReader = basicOARPdfReader;
        this.storageApi = storageApi;
        this.pdfHandleMapper = pdfHandleMapper;
        this.pdfHandleRepository = pdfHandleRepository;
    }

    @Override
    public String readAsString(final byte[] file) {

        try {
            final PDDocument doc = PDDocument.load(file);
            // TODO: add a more general way for getting the right parser based on document template once we have more document types
            final PDFReader pdfReader = basicOARPdfReader;
            return pdfReader.getContent(doc);
        } catch (IOException e) {
            log.warn("Exception while parsing PDF file", e);
            throw LegalValidationException.builder()
                    .i18nKey("document.parse.pdf.failed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @Override
    public PdfHandle createPdf(@NonNull DocumentConsolidated owner, String state, MultipartFile file) {
        Integer hash = file.hashCode();
        if (pdfHandleRepository.existsByHash(hash)) {
            if (pdfHandleRepository.existsByHashAndState(hash, state)) {
                return pdfHandleRepository.findByHashAndState(hash, state);
            }
            final String handleId = pdfHandleRepository.findByHash(hash).getId().toString();
            throw LegalValidationException.builder()
                    .i18nKey("document.pdf.already_exists")
                    .i8nArguments(Collections.singletonList(handleId))
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }

        final String uriString;
        try {
            uriString = storageApi.storeFile(file);
        } catch (Exception e) {
            log.error("failed to save the pdf file", e);
            throw LegalValidationException.builder()
                    .i18nKey("document.pdf.upload.failed")
                    .i8nArguments(Collections.singletonList(e.toString()))
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

        PdfHandle pdfHandle = new PdfHandle();
        pdfHandle.setHash(hash);
        pdfHandle.setOwner(owner);
        pdfHandle.setState(state);
        pdfHandle.setUri(uriString);
        pdfHandle.setTimestamp(Instant.now());

        return pdfHandleRepository.save(pdfHandle);
    }
}
