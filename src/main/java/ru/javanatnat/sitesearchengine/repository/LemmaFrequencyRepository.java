package ru.javanatnat.sitesearchengine.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javanatnat.sitesearchengine.model.LemmaFrequency;

import java.util.Optional;

public interface LemmaFrequencyRepository
        extends
        CrudRepository<LemmaFrequency, Long>,
        CustomLemmaRepository {
    Optional<LemmaFrequency> findByLemma(String lemma);
}
