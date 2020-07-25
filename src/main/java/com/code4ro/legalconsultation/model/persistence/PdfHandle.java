package com.code4ro.legalconsultation.model.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pdf_handle")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfHandle extends BaseEntity {
    @Column(nullable = false)
    private String state;

    @Column(nullable = false, unique = true)
    private String uri;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false, unique = true)
    private Integer hash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consolidated_document_id", nullable = false)
    private DocumentConsolidated owner;
}
