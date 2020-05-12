package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.DocumentConsolidatedDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.math.BigInteger;

@Mapper
public interface DocumentConsolidatedMapper {

    DocumentConsolidatedDto map(DocumentConsolidated documentConsolidated, BigInteger numberOfComments);

    @AfterMapping
    default void setNumberOfComments(@MappingTarget DocumentConsolidatedDto dto, BigInteger numberOfComments) {
        if (dto.getDocumentNode() != null) {
            dto.getDocumentNode().setNumberOfComments(numberOfComments);
        }
    }
}
