package com.code4ro.legalconsultation.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PdfHandleDto extends BaseEntityDto {
    private String state;
    private String uri;
    private Instant timestamp;
    private Integer hash;
}
