package com.code4ro.legalconsultation.model.dto;

import com.code4ro.legalconsultation.model.persistence.DocumentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
public class DocumentMetadataDto extends BaseEntityDto {
    private BigInteger documentNumber;
    private String documentTitle;
    private String documentInitializer;
    private DocumentType documentType;
    private Date dateOfDevelopment;
    private Date dateOfReceipt;
    private String filePath;
}
