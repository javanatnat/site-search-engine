package ru.javanatnat.sitesearchengine.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javanatnat.sitesearchengine.service.SearchService;
import ru.javanatnat.sitesearchengine.service.search.SearchRequest;
import ru.javanatnat.sitesearchengine.service.search.SearchResponse;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    /*
     curl -X 'GET' -H 'Content-Type: application/json'
     --data '{"query":"apple рынок год квартал", "site": "https://lenta.ru", "offset":2, "limit":3}'
     http://localhost:8080/api/search/
     */
    @GetMapping("/")
    public SearchResponse search(@RequestBody SearchRequest searchRequest) {
        LOG.info("Search request: {}", searchRequest);
        SearchResponse result = service.search(searchRequest);
        LOG.info("search result : {}", result);
        return result;
    }
}
