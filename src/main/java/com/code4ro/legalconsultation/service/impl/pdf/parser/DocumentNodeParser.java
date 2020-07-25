package com.code4ro.legalconsultation.service.impl.pdf.parser;

import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.model.persistence.DocumentNodeType;
import com.code4ro.legalconsultation.service.impl.pdf.BoldAreasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.code4ro.legalconsultation.util.StringComparator.containsIgnoreWhitespace;

public abstract class DocumentNodeParser {

    @Lazy
    @Autowired
    private DocumentNodeParserFactory documentNodeParserFactory;
    @Autowired
    private BoldAreasRepository boldAreasRepository;
    @Autowired
    private StartTokenMatcher startTokenMatcher;

    private String[] lines;
    private DocumentParsingMetadata metadata;

    public DocumentNode parse(final String[] lines, final DocumentParsingMetadata metadata) {
        this.lines = lines;
        this.metadata = metadata;

        final DocumentNode documentNode = new DocumentNode();
        documentNode.setDocumentNodeType(getNodeType());

        final StringBuilder contentBuilder = new StringBuilder();

        final Pattern pattern = Pattern.compile(getCurrentToken().regex);
        final String title = getTitle();
        final Matcher titleMatcher = pattern.matcher(title);
        titleMatcher.find();

        documentNode.setIdentifier(getIdentifier(titleMatcher));

        if (hasTitle()) {
            setTitle(documentNode, title, titleMatcher);
        } else {
            contentBuilder.append(getTitle(titleMatcher));
        }

        documentNode.setContent(getNodeContent(contentBuilder));
        documentNode.setChildren(getChildrenNodes(documentNode));

        return documentNode;
    }

    protected void setTitle(final DocumentNode documentNode, final String title, final Matcher titleMatcher) {
        documentNode.setTitle(getTitle(titleMatcher));
    }

    protected abstract String getTitle(final Matcher titleMatcher);

    protected abstract String getIdentifier(final Matcher title);

    protected abstract StartToken getCurrentToken();

    protected abstract DocumentNodeType getNodeType();

    protected abstract Pattern getPatternWithoutTitle();

    protected boolean hasTitle() {
        return true;
    }

    private boolean isSameNodeType(final String line) {
        return startTokenMatcher.startsWith(getCurrentToken(), line);
    }

    private boolean isChildType(final String line) {
        final StartToken lineType = startTokenMatcher.getStartToken(line);
        if (lineType == null) {
            return false;
        }

        return lineType.ordinal() > getCurrentToken().ordinal();
    }

    private String getTitle() {
        String currentLine = getCurrentLine();
        if (isTokenWithoutTitle(currentLine)) {
            metadata.next();
            return currentLine;
        }

        final String result = boldAreasRepository.getMatchingBoldArea(currentLine);
        if (result == null) {
            metadata.next();
            return currentLine;
        }

        skipTitleLines(result);

        return result;
    }

    private String getCurrentLine() {
        return lines[metadata.getCurrentLineIndex()].trim() + " ";
    }

    private void skipTitleLines(final String title) {
        String currentLine = getNextLine();
        while (containsIgnoreWhitespace(title, currentLine)) {
            currentLine = getNextLine();
        }
    }

    public String getNextLine() {
        metadata.next();
        return getCurrentLine();
    }

    private boolean isTokenWithoutTitle(final String currentLine) {
        final Matcher matcher = getPatternWithoutTitle().matcher(currentLine);
        return matcher.matches();
    }

    private String getNodeContent(final StringBuilder contentBuilder) {
        String currentLine = getCurrentLine();
        while (startTokenMatcher.isRegularLine(currentLine)) {
            contentBuilder.append(currentLine);

            if (metadata.isLastLine()) {
                break;
            }

            currentLine = getNextLine();
        }

        final String content = contentBuilder.toString();
        return content.isEmpty() ? null : content;
    }

    private List<DocumentNode> getChildrenNodes(DocumentNode documentNode) {
        final List<DocumentNode> children = new ArrayList<>();
        Integer index = 0;
        String currentLine = lines[metadata.getCurrentLineIndex()];
        while (!isSameNodeType(currentLine) && isChildType(currentLine)) {
            final DocumentNodeParser parser = documentNodeParserFactory.getParser(currentLine);
            final DocumentNode child = parser.parse(lines, metadata);
            child.setParent(documentNode);
            child.setIndex(index);
            index++;
            children.add(child);

            currentLine = lines[metadata.getCurrentLineIndex()];
        }

        return children;
    }
}
