package ru.javanatnat.sitesearchengine.service;

import ru.javanatnat.sitesearchengine.service.search.SearchRequest;
import ru.javanatnat.sitesearchengine.service.search.SearchResponse;

public interface SearchService {
    SearchResponse search(SearchRequest request);
}
