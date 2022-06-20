package ru.javanatnat.sitesearchengine.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.javanatnat.sitesearchengine.service.IndexService;

import java.util.Map;


@RestController
@RequestMapping("/api/index")
public class IndexController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);
    private static final String PAGE_URL = "url";
    private final IndexService service;

    public IndexController(IndexService service) {
        this.service = service;
    }

    // curl -X 'POST' http://localhost:8080/api/index/startIndexing
    @PostMapping("/startIndexing")
    public SimpleBodyResponse startFullIndex() {
        LOG.info("start startFullIndex");
        service.fullIndex();
        LOG.info("end startFullIndex");
        return SimpleBodyResponse.getOk();
    }

    // curl -X 'POST' http://localhost:8080/api/index/stopIndexing
    @PostMapping("/stopIndexing")
    public SimpleBodyResponse stopIndex() {
        LOG.info("start stopIndex");
        service.stopIndex();
        LOG.info("end stopIndex");
        return SimpleBodyResponse.getOk();
    }

    /*
     curl -X 'POST' -H 'Content-Type: application/json' --data '{"url":"https://lenta.ru/news/2022/06/02/applewatch/"}'
     http://localhost:8080/api/index/v1/startIndexing
    */
    @PostMapping("/indexPage")
    public SimpleBodyResponse indexPage(@RequestBody Map<String, String> params) {
        LOG.info("start indexPage, params : {}", params);
        service.indexPage(params.get(PAGE_URL));
        LOG.info("end indexPage");
        return SimpleBodyResponse.getOk();
    }
}
