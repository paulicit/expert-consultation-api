package com.code4ro.legalconsultation.model.dto;

import com.code4ro.legalconsultation.model.dto.documentnode.DocumentNodeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocumentConsolidatedDto extends BaseEntityDto {
    private DocumentMetadataDto documentMetadata;
    private DocumentNodeDto documentNode;
    private DocumentConfigurationDto documentConfiguration;
    private List<UserDto> assignedUsers;
}
