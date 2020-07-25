package com.code4ro.legalconsultation.factory;

import org.apache.pdfbox.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class PdfFileFactory {

    private static final List<String> filenames = Arrays.asList(
            "sample_legal_document.pdf",
            "codul_deontologic_al_oar_2011_pdf_1445359410.pdf",
            "regulament_cadru_2018_pdf_1536138396.pdf",
            "rof_2018_pdf_1536138173.pdf");

    public static MockMultipartFile getAsMultipart(final ClassLoader classLoader, final String filename) {
        try {
            final File file = getAsFile(classLoader, filename);
            final FileInputStream input = new FileInputStream(file);

            return new MockMultipartFile("file",
                    file.getName(), "application/pdf", IOUtils.toByteArray(input));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File getAsFile(final ClassLoader classLoader, final String filename) {
        return new File(classLoader.getResource(String.format("pdf/%s", filename)).getFile());
    }

    public static MockMultipartFile getAsMultipart(final ClassLoader classLoader) {
        try {
            File file = getAsFiles(classLoader).get(0);
            FileInputStream input = new FileInputStream(file);

            return new MockMultipartFile("file",
                    file.getName(), "application/pdf", IOUtils.toByteArray(input));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<File> getAsFiles(final ClassLoader classLoader) {
        return filenames.stream()
                .map(fileName -> new File(classLoader.getResource(String.format("pdf/%s", fileName)).getFile()))
                .collect(Collectors.toList());
    }
}
