package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.PdfHandle;
import com.code4ro.legalconsultation.repository.PdfHandleRepository;
import com.code4ro.legalconsultation.service.impl.FilesystemStorageService;
import com.code4ro.legalconsultation.service.impl.pdf.PDFServiceImpl;
import com.code4ro.legalconsultation.factory.PdfFileFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PdfServiceTest {

    @Mock
    private FilesystemStorageService storageApi;
    @Mock
    private PdfHandleRepository pdfHandleRepository;

    @InjectMocks
    private PDFServiceImpl pdfService;

    @Test
    public void createPdf() throws IOException {
        final UUID ownerUuid = UUID.randomUUID();
        final DocumentConsolidated owner = new DocumentConsolidated();
        owner.setId(ownerUuid);
        
        final UUID pdfHandleId = UUID.randomUUID();
        final String state = "abcdef";
        final MultipartFile randomFile = PdfFileFactory
                .getAsMultipart(getClass().getClassLoader());
        final String randomFilePath = "file:///" + randomFile.getOriginalFilename();
        assertThat(randomFile).isNotNull();
        Instant timestamp = Instant.now();
        final Integer hash = randomFile.hashCode();

        when(pdfHandleRepository.save(any(PdfHandle.class))).thenAnswer((Answer<PdfHandle>) invocationOnMock -> {
            PdfHandle mockPdfHandle = invocationOnMock.getArgument(0);
            mockPdfHandle.setId(pdfHandleId);
            return mockPdfHandle;
        });
        when(storageApi.storeFile(any(MultipartFile.class))).thenReturn(randomFilePath);

        final PdfHandle pdfHandle = pdfService.createPdf(owner, state, randomFile);

        Instant timestampAfter = Instant.now();

        verify(pdfHandleRepository).existsByHash(hash);

        assertThat(pdfHandleId).isEqualTo(pdfHandle.getId());
        assertThat(ownerUuid).isEqualTo(pdfHandle.getOwner().getId());
        assertThat(state).isEqualTo(pdfHandle.getState());
        assertThat(timestamp).isBefore(pdfHandle.getTimestamp());
        assertThat(timestampAfter).isAfterOrEqualTo(pdfHandle.getTimestamp());
        assertThat(hash).isEqualTo(pdfHandle.getHash());
        assertThat(randomFilePath).isEqualTo(pdfHandle.getUri());
    }
}
