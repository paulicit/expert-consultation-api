package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.InvitationDto;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InvitationMapper {
    @Mappings({
            @Mapping(target = "email", source = "user.email")
    })
    InvitationDto map(Invitation invitation);
}
