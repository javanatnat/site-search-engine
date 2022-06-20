package ru.javanatnat.sitesearchengine.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContentEmptyParser extends ContentParser{
    public ContentEmptyParser(
            int codeResponse,
            String errorMessage,
            String url
    ) {
        super(null, codeResponse, errorMessage, url);
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBodyText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSnippet(Set<String> queryWords) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public int getCodeResponse() {
        return codeResponse;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Set<String> getAllContentRefs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getElementText(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean codeResponseIsOk() {
        return false;
    }
}
