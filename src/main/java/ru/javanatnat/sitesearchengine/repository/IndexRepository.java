package ru.javanatnat.sitesearchengine.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.javanatnat.sitesearchengine.model.Index;

import java.util.List;
import java.util.Optional;

public interface IndexRepository
        extends
        CrudRepository<Index, Long>,
        CustomIndexRepository {
    @Query("SELECT count(distinct i.lemma_id) FROM t_index i " +
            "JOIN t_page p ON i.page_id = p.id WHERE p.site_id = :site_id")
    long countDistinctLemmasBySite(
            @Param("site_id") long siteId
    );

    @Query("SELECT distinct i.page_id FROM t_index i WHERE i.lemma_id = :lemma_id")
    List<Long> findDistinctPageIdByLemmaId(
            @Param("lemma_id") long lemmaId
    );

    List<Index> findByPageIdAndLemmaId(
            @Param("page_id") long pageId,
            @Param("lemma_id") long lemmaId
    );

    Optional<Index> findByPageIdAndLemmaIdAndFieldId(
            @Param("page_id") long pageId,
            @Param("lemma_id") long lemmaId,
            @Param("field_id") long fieldId
    );
}
