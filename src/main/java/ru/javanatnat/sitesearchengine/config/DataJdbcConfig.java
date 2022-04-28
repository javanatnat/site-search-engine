package ru.javanatnat.sitesearchengine.config;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.lang.NonNull;
import ru.javanatnat.sitesearchengine.model.SiteStatusType;

import java.sql.SQLException;
import java.util.List;

@Configuration
public class DataJdbcConfig extends AbstractJdbcConfiguration {
    @NonNull
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(
                List.of(new WriteSiteStatusConverter(), new ReadSiteStatusConverter()));
    }

    @WritingConverter
    public static class WriteSiteStatusConverter implements Converter<SiteStatusType, PGobject> {
        private static final Logger LOG = LoggerFactory.getLogger(WriteSiteStatusConverter.class);

        @Override
        public PGobject convert(@NonNull SiteStatusType source) {
            LOG.debug("convert: {}", source);
            PGobject pgobject = new PGobject();
            pgobject.setType("t_site_status");
            try {
                pgobject.setValue(source.toString());
            } catch (SQLException e) {
                LOG.debug("convert: error: {}, {}", e.getSQLState(), e.getMessage());
                throw new RuntimeException(e);
            }
            LOG.debug("convert: SUCCESS");

            return pgobject;
        }
    }

    @ReadingConverter
    public static class ReadSiteStatusConverter implements Converter<PGobject, SiteStatusType> {
        private static final Logger LOG = LoggerFactory.getLogger(ReadSiteStatusConverter.class);

        @Override
        public SiteStatusType convert(PGobject source) {
            String value = source.getValue();
            LOG.debug("convert: {}", value);
            return (value == null) ? null : SiteStatusType.valueOf(value);
        }
    }
}
