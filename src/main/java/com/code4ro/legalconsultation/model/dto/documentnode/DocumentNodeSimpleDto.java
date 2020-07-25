package com.code4ro.legalconsultation.model.dto.documentnode;

import com.code4ro.legalconsultation.model.dto.BaseEntityDto;
import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class DocumentNodeSimpleDto extends BaseEntityDto {
    private UUID parentId;
    private DocumentNodeType documentNodeType;
    @Size(max = 255)
    private String title;
    private String content;
    @Size(max = 255)
    private String identifier;
}
