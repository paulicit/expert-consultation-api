package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.PdfHandleDto;
import com.code4ro.legalconsultation.model.persistence.PdfHandle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PdfHandleMapper {
    PdfHandle map(PdfHandleDto pdfHandleDto);
    PdfHandleDto map(PdfHandle pdfHandle);
}
