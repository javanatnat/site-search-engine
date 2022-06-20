package ru.javanatnat.sitesearchengine.service.index;

public class IndexSiteException extends RuntimeException {
    public IndexSiteException(String message) {
        super(message);
    }

    public IndexSiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
