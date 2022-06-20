package ru.javanatnat.sitesearchengine.service.search;

import org.springframework.stereotype.Service;
import ru.javanatnat.sitesearchengine.model.Index;
import ru.javanatnat.sitesearchengine.model.LemmaFrequency;
import ru.javanatnat.sitesearchengine.model.Page;
import ru.javanatnat.sitesearchengine.model.Site;
import ru.javanatnat.sitesearchengine.repository.IndexRepository;
import ru.javanatnat.sitesearchengine.repository.LemmaFrequencyRepository;
import ru.javanatnat.sitesearchengine.repository.PageRepository;
import ru.javanatnat.sitesearchengine.repository.SiteRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DbSearchServiceImpl implements DbSearchService{
    private final LemmaFrequencyRepository lemmaFrequencyRepository;
    private final IndexRepository indexRepository;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;

    public DbSearchServiceImpl(
            LemmaFrequencyRepository lemmaFrequencyRepository,
            IndexRepository indexRepository,
            PageRepository pageRepository,
            SiteRepository siteRepository
    ) {
        this.lemmaFrequencyRepository = lemmaFrequencyRepository;
        this.indexRepository = indexRepository;
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
    }

    @Override
    public List<LemmaFrequency> getLemmasBySetValues(Set<String> lemmas) {
        return lemmaFrequencyRepository.findAllInSetOrderByFrequencyAsc(lemmas);
    }

    @Override
    public List<Long> getPagesIdByLemma(LemmaFrequency lemma) {
        return indexRepository.findDistinctPageIdByLemmaId(lemma.getId());
    }

    @Override
    public List<Page> getPages(Site site, LemmaFrequency lemma) {
        return pageRepository.findPagesBySiteAndLemma(site.getId(), lemma.getId());
    }

    @Override
    public Optional<Page> getPageById(Long pageId) {
        return pageRepository.findById(pageId);
    }

    @Override
    public Optional<Site> getSite(String url) {
        return siteRepository.findByUrl(url);
    }

    @Override
    public List<Site> getIndexedSites() {
        return siteRepository.findAll()
                .stream()
                .filter(Site::siteIsIndexed)
                .toList();
    }

    @Override
    public List<Index> getIndexByPageAndLemma(Page page, LemmaFrequency lemma) {
        return indexRepository.findByPageIdAndLemmaId(page.getId(), lemma.getId());
    }
}
