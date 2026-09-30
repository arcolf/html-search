package net.arcolf.htmlsearch;

import java.util.ArrayList;
import java.util.List;

public class TextContent {

    private String text;

    private final List<IndexedTextNode> indexedTextNodes;

    public TextContent(String text, List<IndexedTextNode> indexedTextNodes) {
        this.text = text;
        this.indexedTextNodes = indexedTextNodes;
    }

    public String getText() {
        return text;
    }

    public List<IndexedTextNode> getIndexedTextNodes() {
        return indexedTextNodes;
    }

    public List<IndexedTextNode> getIndexedTextNodesBetween(int startIndex, int endIndex) {
        List<IndexedTextNode> result = new ArrayList<>();

        for (IndexedTextNode indexedTextNode : indexedTextNodes) {
            if (indexedTextNode.getStartIndex() >= startIndex && indexedTextNode.getEndIndex() <= endIndex) {
                result.add(indexedTextNode); // Middle node
            }
            else if (indexedTextNode.getStartIndex() <= startIndex && indexedTextNode.getEndIndex() >= startIndex) {
                result.add(indexedTextNode); // First node
            } else if (indexedTextNode.getStartIndex() <= endIndex && indexedTextNode.getEndIndex() >= endIndex) {
                result.add(indexedTextNode); // Last node
            }
        }

        return result;
    }

    public void updateText(String newText) {
        int diff = text.length() - newText.length();
        text = newText;

        indexedTextNodes.forEach(indexedTextNode -> {
            indexedTextNode.shiftIndex(diff);
        });
    }
}
