package ru.javanatnat.sitesearchengine.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import ru.javanatnat.sitesearchengine.model.LemmaFrequency;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class CustomLemmaRepositoryImpl implements CustomLemmaRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CustomLemmaRepositoryImpl.class);

    private static final String PARAM_LIST_LEMMAS = "lemmas";
    private static final String FIELD_ID = "id";
    private static final String FIELD_LEMMA = "lemma";
    private static final String FIELD_FREQUENCY = "frequency";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_FIND_ALL_IN_ORDER_BY_FREQUENCY =
            "SELECT id, lemma, frequency " +
            "FROM t_lemma_frequency " +
            "WHERE lemma in (:lemmas) " +
            "ORDER BY frequency";

    private static final String SQL_FIND_ALL_IN =
            "SELECT id, lemma, frequency " +
                    "FROM t_lemma_frequency " +
                    "WHERE lemma in (:lemmas)";

    private static final String SQL_BATCH_INSERT =
            "INSERT INTO t_lemma_frequency AS t (lemma, frequency) " +
                    "VALUES (?, 1) " +
                    "ON CONFLICT (lemma) " +
                    "DO UPDATE SET frequency = t.frequency + 1";

    public CustomLemmaRepositoryImpl(
            NamedParameterJdbcTemplate namedJdbcTemplate,
            JdbcTemplate jdbcTemplate
    ) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LemmaFrequency> findAllInSetOrderByFrequencyAsc(Set<String> lemmas) {
        return findAllParamSQL(SQL_FIND_ALL_IN_ORDER_BY_FREQUENCY, lemmas);
    }

    @Override
    public List<LemmaFrequency> findAllInSet(Set<String> lemmas) {
        return findAllParamSQL(SQL_FIND_ALL_IN, lemmas);
    }

    private List<LemmaFrequency> findAllParamSQL(String sql, Set<String> lemmas) {
        SqlParameterSource parameters = new MapSqlParameterSource(PARAM_LIST_LEMMAS, lemmas);

        List<LemmaFrequency> lemmaFrequencyList = namedJdbcTemplate.query(
                sql,
                parameters,
                (rs, row) -> new LemmaFrequency(
                        rs.getLong(FIELD_ID),
                        rs.getString(FIELD_LEMMA),
                        rs.getInt(FIELD_FREQUENCY)));

        LOG.debug("lemmaFrequencyList: {}", lemmaFrequencyList.size());
        return lemmaFrequencyList;
    }

    @Override
    public void batchInsert(List<String> lemmas) {
        jdbcTemplate.batchUpdate(
                SQL_BATCH_INSERT,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, lemmas.get(i));
                    }
                    @Override
                    public int getBatchSize() {
                        return lemmas.size();
                    }
                });
    }
}
