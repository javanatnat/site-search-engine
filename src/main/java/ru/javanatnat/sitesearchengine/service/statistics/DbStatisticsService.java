package ru.javanatnat.sitesearchengine.service.statistics;

import ru.javanatnat.sitesearchengine.model.Site;

import java.util.List;

public interface DbStatisticsService {
    long getSitesCount();
    long getPagesCount();
    long getLemmasCount();
    List<Site> getAllSites();
    long getPagesCountBySite(Site site);
    long getCountLemmasBySite(Site site);
}
