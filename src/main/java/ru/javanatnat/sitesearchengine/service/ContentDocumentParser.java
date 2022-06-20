package ru.javanatnat.sitesearchengine.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.sitesearchengine.service.index.IndexSiteException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class ContentDocumentParser extends ContentParser{
    private static final Logger LOG = LoggerFactory.getLogger(ContentDocumentParser.class);

    private static final String TAG_TITLE = "title";
    private static final String TAG_BODY = "body";
    private static final String TAG_A = "a";
    private static final String ATTR_HREF = "href";
    private static final String DELIMITER_REGEXP = "\\s+";
    private static final int COUNT_SNIPPET_WORDS = 30;

    public ContentDocumentParser(
            Document document
    ) {
        super(document, HttpURLConnection.HTTP_OK, null, null);
    }

    public ContentDocumentParser(
            Document document,
            String url
    ) {
        super(document, HttpURLConnection.HTTP_OK, null, url);
    }

    @Override
    public String getTitle() {
        return document.title();
    }

    @Override
    public String getBodyText() {
        return document.body().text();
    }

    @Override
    public String getSnippet(Set<String> queryWords) {
        if (queryWords == null || queryWords.isEmpty()) {
            return null;
        }

        LOG.debug("getSnippet, find by words: {}", queryWords);
        String title = getTitle();
        LOG.debug("getSnippet, title: {}", title);

        boolean wordsInTitle = queryWords.stream().anyMatch(title::contains);
        if (wordsInTitle) {
            LOG.debug("getSnippet, return title");
            return title;
        }

        return getSnippetFromBody(queryWords);
    }

    @Override
    public String getContent() {
        return document.outerHtml();
    }

    @Override
    public int getCodeResponse() {
        return codeResponse;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public Set<String> getAllContentRefs() {
        try {
            org.jsoup.select.Elements tags = getAllTagsA();
            return tags.stream()
                    .map(e -> e.attr(ATTR_HREF))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IndexSiteException("Ошибка при чтении текста страницы " + url, e);
        }
    }

    @Override
    public String getElementText(String selector) {
        if (selector.equals(TAG_TITLE)) {
            return getTitle();
        } else if (selector.equals(TAG_BODY)) {
            return getBodyText();
        }
        return getElementConcatText(selector);
    }

    @Override
    public boolean codeResponseIsOk() {
        return true;
    }

    private Elements getAllTagsA()
            throws IOException, IllegalArgumentException {
        return document.getElementsByTag(TAG_A);
    }

    private String getElementConcatText(String selector) {
        Elements elements = document.getElementsByTag(selector);
        if (!elements.isEmpty()) {
            StringBuilder text = new StringBuilder();
            for (Element el : elements) {
                text.append(el.text());
                text.append(" ");
            }
            return text.toString();
        }
        return null;
    }

    private String getSnippetFromBody(Set<String> queryWords) {
        String body = getBodyText();
        String[] bodyWords = body.split(DELIMITER_REGEXP);
        LOG.debug("bodyWords = {}", Arrays.toString(bodyWords));

        for(String word : queryWords) {
            LOG.debug("getSnippetFromBody, word: {}", word);
            if (Arrays.stream(bodyWords).noneMatch(bw -> bw.toLowerCase(Locale.ROOT).contains(word))) {
                LOG.debug("getSnippetFromBody, no match with body");
                continue;
            }

            int firstIndex = -1;
            for (int i = 0; i < bodyWords.length; i++) {
                if (bodyWords[i].contains(word)) {
                    firstIndex = i;
                    break;
                }
            }
            LOG.debug("getSnippetFromBody, index of first suitable word: {}", firstIndex);

            if (firstIndex >= 0) {
                int i = firstIndex;
                int lastIndex = Math.min(bodyWords.length, COUNT_SNIPPET_WORDS + firstIndex);
                StringBuilder builder = new StringBuilder(lastIndex - firstIndex);
                while (i < lastIndex) {
                    builder.append(bodyWords[i]);
                    builder.append(" ");
                    i++;
                }
                String result = builder.toString();
                LOG.debug("getSnippetFromBody, return: {}", result);
                return result;
            }
        }

        LOG.debug("getSnippetFromBody, return null");
        return null;
    }

}
