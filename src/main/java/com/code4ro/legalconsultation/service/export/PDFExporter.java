package com.code4ro.legalconsultation.service.export;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class PDFExporter implements DocumentExporter {
    private static final String OPEN_SANS_FONT_FILEPATH = "src/main/resources/fonts/OpenSans.ttf";
    private final TemplateEngine templateEngine;

    @Autowired
    public PDFExporter(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] export(final DocumentConsolidated document) {
        final DocumentNode rootNode = document.getDocumentNode();
        final Context ctx = new Context();
        ctx.setVariable("rootNode", rootNode);

        final String processedHtml = templateEngine.process("basic-template", ctx);

        try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            final ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.getFontResolver().addFont(OPEN_SANS_FONT_FILEPATH, BaseFont.IDENTITY_H, true);
            renderer.layout();
            renderer.createPDF(os, true);

            return os.toByteArray();
        } catch (IOException | DocumentException e) {
            throw LegalValidationException.builder()
                    .i18nKey("document.export.failed")
                    .i8nArguments(Collections.singletonList(e.getMessage()))
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
