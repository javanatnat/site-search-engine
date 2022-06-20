package ru.javanatnat.sitesearchengine.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.javanatnat.sitesearchengine.model.Site;
import ru.javanatnat.sitesearchengine.service.index.IndexSiteException;

public class CustomSiteRepositoryImpl implements CustomSiteRepository{
    private static final Logger LOG = LoggerFactory.getLogger(CustomSiteRepositoryImpl.class);

    private static final String INSERT_QUERY =
            "INSERT INTO t_site(url, name, last_error, status, status_time) " +
            "VALUES(:url, :name, :last_error, :status::t_site_status, :status_time)";
    private static final String UPDATE_QUERY =
            "UPDATE t_site SET " +
            "last_error = :last_error, " +
            "status = :status::t_site_status, " +
            "status_time = :status_time " +
            "WHERE id = :id";

    private static final String ID = "id";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String LAST_ERROR = "last_error";
    private static final String STATUS = "status";
    private static final String STATUS_TIME = "status_time";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public CustomSiteRepositoryImpl(
            NamedParameterJdbcTemplate namedJdbcTemplate
    ) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public Site customSave(Site site) {
        LOG.debug("start save site: {}", site);
        if (site.getId() == null) {
            return insertSite(site);
        } else {
            return updateSite(site);
        }
    }

    private Site insertSite(Site site) {
        LOG.debug("start insert site: {}", site);

        KeyHolder holder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(URL, site.getUrl())
                .addValue(NAME, site.getName())
                .addValue(LAST_ERROR, site.getLastError())
                .addValue(STATUS, site.getStatus().name())
                .addValue(STATUS_TIME, site.getStatusTime());

        int countInsert = namedJdbcTemplate.update(INSERT_QUERY, parameters, holder, new String[] {ID});
        if (countInsert == 1) {
            Number generatedId = holder.getKey();
            LOG.debug("Generated id: {}", generatedId);
            return new Site(
                    (Long) generatedId,
                    site.getStatus(),
                    site.getStatusTime(),
                    site.getLastError(),
                    site.getUrl(),
                    site.getName());
        }

        String errorMsg = "Ошибка вставки сайта " + site + " в базу данных! Данные не добавлены!";
        LOG.error(errorMsg);
        throw new IndexSiteException(errorMsg);
    }

    private Site updateSite(Site site) {
        LOG.debug("start update site: {}", site);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(LAST_ERROR, site.getLastError())
                .addValue(STATUS, site.getStatus().name())
                .addValue(STATUS_TIME, site.getStatusTime())
                .addValue(ID, site.getId());

        int countUpdate = namedJdbcTemplate.update(UPDATE_QUERY, parameters);
        if (countUpdate == 1) {
            return site;
        }

        String errorMsg = "Ошибка обновления сайта " + site + " в базе данных! Данные не обновлены!";
        LOG.error(errorMsg);
        throw new IndexSiteException(errorMsg);
    }
}
