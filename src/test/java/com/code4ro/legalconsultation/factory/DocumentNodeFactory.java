package com.code4ro.legalconsultation.factory;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import com.code4ro.legalconsultation.repository.DocumentNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DocumentNodeFactory {

    @Autowired
    private DocumentNodeRepository documentNodeRepository;

    public DocumentNode save() {
        return documentNodeRepository.save(create());
    }

    public DocumentNode create() {
        final DocumentNode root = new DocumentNode();
        root.setDocumentNodeType(DocumentNodeType.DOCUMENT);
        final DocumentNode child = new DocumentNode();
        child.setDocumentNodeType(DocumentNodeType.ARTICLE);
        List<DocumentNode> nodeChildren = new ArrayList<>();
        nodeChildren.add(child);
        root.setChildren(nodeChildren);
        child.setParent(root);
        return root;
    }

    public DocumentNode createChapter(final String identifier, final String title, final String content) {
        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(DocumentNodeType.CHAPTER);
        return createDocumentNode(documentNode, identifier, title, content);
    }

    public DocumentNode createDocument(final String identifier, final String title, final String content) {
        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(DocumentNodeType.DOCUMENT);
        return createDocumentNode(documentNode, identifier, title, content);
    }

    public DocumentNode createArticle(final String identifier, final String title, final String content) {
        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(DocumentNodeType.ARTICLE);
        return createDocumentNode(documentNode, identifier, title, content);
    }

    public DocumentNode createParagraph(final String identifier, final String title, final String content) {
        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(DocumentNodeType.PARAGRAPH);
        return createDocumentNode(documentNode, identifier, title, content);
    }

    public DocumentNode createAlignment(final String identifier, final String title, final String content) {
        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(DocumentNodeType.ALIGNMENT);
        return createDocumentNode(documentNode, identifier, title, content);
    }

    public DocumentNode createSection(final String identifier, final String title, final String content) {
        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(DocumentNodeType.SECTION);
        return createDocumentNode(documentNode, identifier, title, content);
    }

    public DocumentNode createDocumentNode(final DocumentNode documentNode,
                                           final String identifier,
                                           final String title,
                                           final String content) {
        documentNode.setIdentifier(identifier);
        documentNode.setTitle(title);
        documentNode.setContent(content);

        return documentNode;
    }
}
