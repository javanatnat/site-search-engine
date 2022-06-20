package ru.javanatnat.sitesearchengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.javanatnat.sitesearchengine.model.Site;
import ru.javanatnat.sitesearchengine.service.statistics.*;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService{
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);
    private final DbStatisticsService dbService;

    public StatisticsServiceImpl(
            DbStatisticsService dbService
    ) {
        this.dbService = dbService;
    }

    @Override
    public Map<String, Object> getStatistics() {
        LOG.info("start get statistics...");
        try {
            long siteCount = dbService.getSitesCount();
            LOG.debug("siteCount: {}", siteCount);

            long pageCount = dbService.getPagesCount();
            LOG.debug("pageCount: {}", pageCount);

            long lemmaCount = dbService.getLemmasCount();
            LOG.debug("lemmaCount: {}", lemmaCount);

            boolean isIndexing = (lemmaCount > 0);
            LOG.debug("isIndexing: {}", isIndexing);

            StatisticsTotal total = new StatisticsTotal(
                    siteCount,
                    pageCount,
                    lemmaCount,
                    isIndexing
            );
            StatisticsResponse response = new StatisticsResponse(total);

            if (isIndexing) {
                List<Site> allSites = dbService.getAllSites();
                LOG.debug("all sites: {}", allSites);

                for (Site site : allSites) {
                    LOG.debug("site: {}", site);
                    long sitePages = dbService.getPagesCountBySite(site);
                    LOG.debug("sitePages: {}", sitePages);

                    long siteLemmas = dbService.getCountLemmasBySite(site);
                    LOG.debug("siteLemmas: {}", siteLemmas);

                    StatSiteDetails siteDetails = new StatSiteDetails.Builder(site.getUrl())
                            .setName(site.getName())
                            .setStatus(site.getStatus().name())
                            .setStatusTime(site.getStatusTime())
                            .setError(site.getLastError())
                            .setPages(sitePages)
                            .setLemmas(siteLemmas)
                            .build();

                    response.addSiteDetails(siteDetails);
                }
            }

            Map<String, Object> result = response.getResponse();
            LOG.info("result: {}", result);
            LOG.info("end get statistics.");

            return result;

        } catch (Exception e) {
            LOG.error("Exception while getStatistics: {}", e.toString());
            throw new GetStatException();
        }
    }
}
