package ru.javanatnat.sitesearchengine.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javanatnat.sitesearchengine.service.StatisticsService;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticController.class);
    private final StatisticsService service;

    public StatisticController(StatisticsService service) {
        this.service = service;
    }

    // curl -X 'GET' http://localhost:8080/api/statistics/
    @GetMapping("/")
    public Map<String, Object> getStatistics() {
        LOG.info("getStatistics");
        return service.getStatistics();
    }
}
