package net.arcolf.htmlsearch;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

public class HtmlSearchUtils {

    public static TextContent createTextContent(List<Node> nodes, int startIndex) {
        StringBuilder sb = new StringBuilder();
        List<IndexedTextNode> indexedNodes = new ArrayList<>();

        for (Node node : nodes) {
            if (node instanceof TextNode t) {
                sb.append(t.text());
                indexedNodes.add(new IndexedTextNode(t, startIndex));
                startIndex += t.text().length();
            } else if (node instanceof Element e) {
                TextContent childContent = createTextContent(e.childNodes(), startIndex);
                sb.append(childContent.getText());
                indexedNodes.addAll(childContent.getIndexedTextNodes());
                startIndex += childContent.getText().length();
            }

        }

        return new TextContent(sb.toString(), indexedNodes);
    }

    public static void searchAndReplace(TextContent textContent, SearchParams params) {
        String currentText = textContent.getText();
        int startIndex = 0;
        while (startIndex < currentText.length()) {
            startIndex = StringUtils.indexOf(currentText, params.search(), startIndex);
            if (startIndex == -1) {
                break;
            }

            int endIndex = startIndex + params.search().length();
            List<IndexedTextNode> textNodesOfSearch = textContent.getIndexedTextNodesBetween(startIndex, endIndex);
            handleFirstTextNodeOfSearch(textNodesOfSearch.get(0), startIndex, params.replacement());
            if (textNodesOfSearch.size() > 2) {
                for (int i = 1; i < textNodesOfSearch.size() - 1; i++) {
                    removeTextNode(textNodesOfSearch.get(i));
                }
            }
            if (textNodesOfSearch.size() > 1) {
                handleLastTextNodeOfSearch(textNodesOfSearch.get(textNodesOfSearch.size() - 1), endIndex);
            }

            currentText = StringUtils.replaceOnce(currentText, params.search(), params.replacement());
            textContent.updateText(currentText);
            startIndex += params.replacement().length();
        }

    }

    public static void handleFirstTextNodeOfSearch(IndexedTextNode currentTextNode, int startPosition, String replace) {
        String oldText = currentTextNode.getTextNode().text();
        String newText = oldText.substring(0, startPosition - currentTextNode.getStartIndex());
        currentTextNode.getTextNode().text(newText + replace);
    }

    private static void removeTextNode(IndexedTextNode currentTextNode) {
        Element parent = currentTextNode.getTextNode().parent();
        currentTextNode.getTextNode().remove();

        removeEmptyElements(parent);
    }

    public static void handleLastTextNodeOfSearch(IndexedTextNode currentTextNode, int endPosition) {
        String oldText = currentTextNode.getTextNode().text();
        String newText = oldText.substring(endPosition - currentTextNode.getStartIndex());
        if (! newText.isEmpty()) {
            currentTextNode.getTextNode().text(newText);
        } else {
            removeTextNode(currentTextNode);
        }
    }

    public static void removeEmptyElements(Element element) {
        if ((! element.childNodes().isEmpty()) || (! element.attributes().isEmpty())) {
            return;
        }
        Element parent = element.parent();
        element.remove();
        removeEmptyElements(parent);
    }
}
