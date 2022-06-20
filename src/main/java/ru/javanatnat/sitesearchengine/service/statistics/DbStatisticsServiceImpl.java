package ru.javanatnat.sitesearchengine.service.statistics;

import org.springframework.stereotype.Service;
import ru.javanatnat.sitesearchengine.model.Site;
import ru.javanatnat.sitesearchengine.repository.IndexRepository;
import ru.javanatnat.sitesearchengine.repository.LemmaFrequencyRepository;
import ru.javanatnat.sitesearchengine.repository.PageRepository;
import ru.javanatnat.sitesearchengine.repository.SiteRepository;

import java.util.List;

@Service
public class DbStatisticsServiceImpl implements DbStatisticsService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaFrequencyRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public DbStatisticsServiceImpl(
            SiteRepository siteRepository,
            PageRepository pageRepository,
            LemmaFrequencyRepository lemmaRepository,
            IndexRepository indexRepository
    ) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public long getSitesCount() {
        return siteRepository.count();
    }

    @Override
    public long getPagesCount() {
        return pageRepository.count();
    }

    @Override
    public long getLemmasCount() {
        return lemmaRepository.count();
    }

    @Override
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    @Override
    public long getPagesCountBySite(Site site) {
        return pageRepository.countBySiteId(site.getId());
    }

    @Override
    public long getCountLemmasBySite(Site site) {
        return indexRepository.countDistinctLemmasBySite(site.getId());
    }
}
