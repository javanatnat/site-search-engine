package ru.javanatnat.sitesearchengine.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.javanatnat.sitesearchengine.model.Page;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends CrudRepository<Page, Long> {
    long countBySiteId(
            @Param("site_id") long siteId
    );
    Optional<Page> findBySiteIdAndPath(
            @Param("site_id") long siteId,
            @Param("path") String path
    );

    @Query("SELECT distinct p.* " +
            "FROM t_page p " +
            "JOIN t_index i " +
            "ON i.page_id = p.id " +
            "WHERE p.site_id = :site_id " +
            "AND i.lemma_id = :lemma_id")
    List<Page> findPagesBySiteAndLemma(
            @Param("site_id") long siteId,
            @Param("lemma_id") long lemmaId
    );
}
