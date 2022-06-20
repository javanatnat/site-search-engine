package ru.javanatnat.sitesearchengine.repository;

import ru.javanatnat.sitesearchengine.model.LemmaFrequency;

import java.util.List;
import java.util.Set;

public interface CustomLemmaRepository {
    List<LemmaFrequency> findAllInSetOrderByFrequencyAsc(Set<String> lemmas);
    List<LemmaFrequency> findAllInSet(Set<String> lemmas);
    void batchInsert(List<String> lemmas);
}
