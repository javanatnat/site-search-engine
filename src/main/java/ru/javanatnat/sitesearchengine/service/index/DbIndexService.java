package ru.javanatnat.sitesearchengine.service.index;

import ru.javanatnat.sitesearchengine.model.*;

import java.util.List;
import java.util.Set;

public interface DbIndexService {
    List<Field> getFields();
    Site getSite(String pageUrl);
    List<LemmaFrequency> getLemmas(Set<String> lemmas);
    Site saveSite(Site site);
    void batchInsertLemmas(Set<String> words);
    void batchInsertIndex(List<Index> index);
    Page savePage(
            String pageUrl,
            Site site,
            int codeResponse,
            String content
    );
    void deleteAllData();
}
