package net.arcolf.htmlsearch;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HtmlSearch {
    private final Element element;

    private final boolean fragment;

    public static HtmlSearch in(String bodyFragment) {
        Objects.requireNonNull(bodyFragment, "Parameter 'bodyFragment' must not be null");

        return new HtmlSearch(bodyFragment);
    }

    public static HtmlSearch in(Element element) {
        Objects.requireNonNull(element, "Parameter 'element' must not be null");

        return new HtmlSearch(element);
    }

    private HtmlSearch(String bodyFragment) {
        Document doc = Jsoup.parseBodyFragment(bodyFragment);
        element = doc.body();
        fragment = true;
    }

    private HtmlSearch(Element element) {
        this.element = element;
        fragment = false;
    }

    public Element getResultElement() {
        return element;
    }

    public String getResultString() {
        if (fragment) {
            return element.html();
        } else {
            return element.outerHtml();
        }
    }

    public HtmlSearch searchAndReplace(String search, String replacement) {
        if (StringUtils.isEmpty(search)) {
            throw new IllegalArgumentException("Parameter 'search' must not be null or empty");
        }
        Objects.requireNonNull(replacement, "Parameter 'replacement' must not be null");

        SearchParams params = new SearchParams(search, replacement);

        searchAndReplaceInElement(element, params);
        return this;
    }

    private void searchAndReplaceInElement(Element currentElement, SearchParams params) {
        for (Node child : currentElement.childNodes()) {
            if (child instanceof Element e) {
                searchAndReplaceInElement(e, params);
            } else if (child instanceof TextNode t) {
                // Simple replacement within a single text node
                t.text(t.text().replace(params.search(), params.replacement()));
            }
        }

        // Early exit if the element has no children or only one TextNode as child
        // The text in the single TextNode has already been replaced
        if ((currentElement.childNodes().isEmpty()) ||
                ((currentElement.childNodes().isEmpty()) && (currentElement.textNodes().size() == 1))) {
            return;
        }

        List<Node> nodes = new ArrayList<>();
        for (Node node : currentElement.childNodes()) {
            if (node instanceof TextNode) {
                nodes.add(node);
            } else if (node instanceof Element e) {
                if (e.isBlock() && (! nodes.isEmpty())) {
                    TextContent content = HtmlSearchUtils.createTextContent(nodes, 0);
                    HtmlSearchUtils.searchAndReplace(content, params);
                    nodes.clear();
                } else {
                    nodes.add(node);
                }
            }
            // Other node types like comment, CDATA are
        }
        if (! nodes.isEmpty()) {
            TextContent content = HtmlSearchUtils.createTextContent(nodes, 0);
            HtmlSearchUtils.searchAndReplace(content, params);
        }
    }

}
