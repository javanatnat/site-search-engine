package ru.javanatnat.sitesearchengine.service.search;

import ru.javanatnat.sitesearchengine.model.Index;
import ru.javanatnat.sitesearchengine.model.LemmaFrequency;
import ru.javanatnat.sitesearchengine.model.Page;
import ru.javanatnat.sitesearchengine.model.Site;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DbSearchService {
    List<LemmaFrequency> getLemmasBySetValues(Set<String> lemmas);
    List<Long> getPagesIdByLemma(LemmaFrequency lemma);
    List<Page> getPages(Site site, LemmaFrequency lemma);
    Optional<Page> getPageById(Long pageId);
    Optional<Site> getSite(String url);
    List<Site> getIndexedSites();
    List<Index> getIndexByPageAndLemma(Page page, LemmaFrequency lemma);
}
