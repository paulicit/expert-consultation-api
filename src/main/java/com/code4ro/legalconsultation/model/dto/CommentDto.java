package com.code4ro.legalconsultation.model.dto;

import com.code4ro.legalconsultation.model.persistence.CommentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentDto extends BaseEntityDto {
    private String text;
    private Date lastEditDateTime;
    private CommentStatus status;
}
