package net.arcolf.htmlsearch;

import org.jsoup.nodes.TextNode;

/**
 * An {@code IndexedTextNode} contains the text node and the
 */
public class IndexedTextNode {

    private final TextNode textNode;

    private int startIndex;

    public IndexedTextNode(TextNode textNode, int startIndex) {
        this.textNode = textNode;
        this.startIndex = startIndex;
    }

    public TextNode getTextNode() {
        return textNode;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return startIndex + textNode.text().length();
    }

    public void shiftIndex(int diff) {
        startIndex += diff;
    }
}
