package ru.javanatnat.sitesearchengine.service.statistics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsResponse {
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_STAT = "statistics";

    private final StatisticsTotal total;
    @JsonProperty("detailed")
    private final List<StatSiteDetails> details;

    public StatisticsResponse(
            StatisticsTotal total,
            List<StatSiteDetails> details
    ) {
        this.total = total;
        this.details = details;
    }

    public StatisticsResponse(StatisticsTotal total) {
        this.total = total;
        this.details = new ArrayList<>();
    }

    public void addSiteDetails(StatSiteDetails siteDetails) {
        details.add(siteDetails);
    }

    public StatisticsTotal getTotal() {
        return total;
    }

    public List<StatSiteDetails> getDetails() {
        return details;
    }

    @JsonIgnore
    public Map<String, Object> getResponse() {
        return Map.of(
                FIELD_RESULT, true,
                FIELD_STAT, this
        );
    }

    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "total=" + total +
                ", details=" + details +
                '}';
    }
}
