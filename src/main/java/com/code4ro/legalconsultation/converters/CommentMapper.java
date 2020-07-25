package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.CommentDetailDto;
import com.code4ro.legalconsultation.model.dto.CommentDto;
import com.code4ro.legalconsultation.model.persistence.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto map(Comment comment);

    Comment map(CommentDto commentDto);

    @Mappings({
            @Mapping(target = "user", source = "comment.owner.name")
    })
    CommentDetailDto mapToCommentDetailDto(Comment comment);
}
