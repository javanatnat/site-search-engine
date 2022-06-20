package ru.javanatnat.sitesearchengine.service;

public interface IndexService {
    void fullIndex();
    void stopIndex();
    void indexPage(String url);
}
