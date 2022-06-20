package ru.javanatnat.sitesearchengine.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import ru.javanatnat.sitesearchengine.model.Index;

import java.util.List;

public class CustomIndexRepositoryImpl implements CustomIndexRepository {

    private static final String SQL_BATCH_INSERT =
            "INSERT INTO t_index (page_id, field_id, lemma_id, index_rank) " +
                    "VALUES (:pageId, :fieldId, :lemmaId, :indexRank)";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public CustomIndexRepositoryImpl(
            NamedParameterJdbcTemplate namedJdbcTemplate
    ) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public void batchInsert(List<Index> index) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(index);
        namedJdbcTemplate.batchUpdate(SQL_BATCH_INSERT, batch);
    }
}
