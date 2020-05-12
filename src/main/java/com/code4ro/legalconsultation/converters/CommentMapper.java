package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.CommentDto;
import com.code4ro.legalconsultation.model.dto.CommentIdentificationDto;
import com.code4ro.legalconsultation.model.persistence.Comment;
import org.mapstruct.Mapper;

@Mapper
public interface CommentMapper {
    CommentDto map(Comment comment);

    Comment map(CommentDto commentDto);

    CommentIdentificationDto mapToCommentIdentificationDto(Comment commentDto);
}
