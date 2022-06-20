package ru.javanatnat.sitesearchengine.service.search;

public class SearchSiteException extends RuntimeException {
    public SearchSiteException(String message) {
        super(message);
    }

    public SearchSiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
