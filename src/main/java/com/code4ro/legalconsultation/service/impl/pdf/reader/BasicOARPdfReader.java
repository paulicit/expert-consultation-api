package com.code4ro.legalconsultation.service.impl.pdf.reader;

import com.code4ro.legalconsultation.service.impl.pdf.BoldAreasRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

@Component
public class BasicOARPdfReader implements PDFReader {
    private static final double FIRST_PAGE_REGION_X_POSITION = 0;
    private static final double FIRST_PAGE_REGION_Y_POSITION = 200;
    private static final double FIRST_PAGE_REGION_WIDTH = 700;
    private static final double FIRST_PAGE_REGION_HEIGHT = 550;
    private static final double REGULAR_PAGE_REGION_X_POSITION = 0;
    private static final double REGULAR_PAGE_REGION_Y_POSITION = 100;
    private static final double REGULAR_PAGE_REGION_WIDTH = 480;
    private static final double REGULAR_PAGE_REGION_HEIGHT = 650;
    private static final String FIRST_PAGE_REGION_NAME = "firstPageRegion";
    private static final String REGULAR_PAGE_REGION_NAME = "regularPageRegion";

    private final Rectangle2D firstPageRegion = new Rectangle2D.Double(FIRST_PAGE_REGION_X_POSITION, FIRST_PAGE_REGION_Y_POSITION, FIRST_PAGE_REGION_WIDTH, FIRST_PAGE_REGION_HEIGHT);
    private final Rectangle2D regularPageRegion = new Rectangle2D.Double(REGULAR_PAGE_REGION_X_POSITION, REGULAR_PAGE_REGION_Y_POSITION, REGULAR_PAGE_REGION_WIDTH, REGULAR_PAGE_REGION_HEIGHT);

    private final BoldAreasRepository boldAreasRepository;
    private final PDFBoldTextStripperByArea boldTextStripperByArea;

    @Autowired
    public BasicOARPdfReader(final BoldAreasRepository boldAreasRepository,
                             final PDFBoldTextStripperByArea boldTextStripperByArea) {
        this.boldAreasRepository = boldAreasRepository;
        this.boldTextStripperByArea = boldTextStripperByArea;
    }

    public String getContent(PDDocument document) throws IOException {
        boldAreasRepository.clear();
        boldTextStripperByArea.getRegions().clear();
        final String result = getContentAsString(document, boldTextStripperByArea);
        boldAreasRepository.setBoldAreas(boldTextStripperByArea.getBoldAreas());
        return result;
    }

    private String getContentAsString(final PDDocument document,
                                      final PDFBoldTextStripperByArea stripper) throws IOException {
        final int numberOfPages = document.getNumberOfPages();
        final StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < numberOfPages; i++) {
            resultBuilder.append(getContentAsStringForPage(document, stripper, i));
            resultBuilder.append("\n");
        }

        return resultBuilder.toString();
    }

    private String getContentAsStringForPage(final PDDocument document,
                                             final PDFBoldTextStripperByArea stripper,
                                             final int pageNumber) throws IOException {
        final PDPage page = document.getPage(pageNumber);
        if (pageNumber == 0) {
            stripper.addRegion(FIRST_PAGE_REGION_NAME, firstPageRegion);
            stripper.extractRegions(page);
            final String result = getContentAsStringForRegion(stripper, FIRST_PAGE_REGION_NAME);
            stripper.removeRegion(FIRST_PAGE_REGION_NAME);
            return result;
        }

        stripper.addRegion(REGULAR_PAGE_REGION_NAME, regularPageRegion);
        stripper.extractRegions(page);
        final String result = getContentAsStringForRegion(stripper, REGULAR_PAGE_REGION_NAME);
        stripper.removeRegion(REGULAR_PAGE_REGION_NAME);
        return result;
    }

    private String getContentAsStringForRegion(final PDFBoldTextStripperByArea stripper, final String regionName) {
        final String textForRegion = stripper.getTextForRegion(regionName);
        return textForRegion.trim();
    }
}
