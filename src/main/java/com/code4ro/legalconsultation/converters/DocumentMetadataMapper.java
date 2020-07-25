package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.DocumentMetadataDto;
import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMetadataMapper {
    DocumentMetadata map(DocumentMetadataDto documentMetadataDto);

    DocumentMetadata map(DocumentViewDto documentViewDto);

    DocumentMetadataDto map(DocumentMetadata documentMetadata);
}
