package com.code4ro.legalconsultation.model.persistence;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "document_nodes")
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentNode extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    @ToString.Exclude
    private DocumentNode parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DocumentNode> children;

    @Column(name = "document_node_type")
    private DocumentNodeType documentNodeType;

    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "identifier")
    private String identifier;
}
