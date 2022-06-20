package ru.javanatnat.sitesearchengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.javanatnat.sitesearchengine.service.index.IndexSiteException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "application")
public class SiteConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SiteConfig.class);

    private List<SiteParam> sites;

    public SiteConfig() {}

    public SiteConfig(List<SiteParam> sites) {
        this.sites = sites;
        checkSiteConfig();
    }

    public boolean pageInSiteConfig(String pageUrl) {
        LOG.info("pageInSiteConfig: pageUrl = {}", pageUrl);
        checkPageUrl(pageUrl);

        boolean isInConfig = sites
                .stream()
                .map(SiteParam::getUrl)
                .anyMatch(s -> s.startsWith(pageUrl));

        LOG.info("pageInSiteConfig: result = {}", isInConfig);
        return isInConfig;
    }

    public Optional<String> getSiteUrl(String pageUrl) {
        return sites
                .stream()
                .map(SiteParam::getUrl)
                .filter(s -> s.startsWith(pageUrl))
                .findAny();
    }

    public void setSites(List<SiteParam> sites) {
        this.sites = sites;
        checkSiteConfig();
    }

    public List<SiteParam> getSites() {
        LOG.debug("getSites from config: {}", sites);
        return Collections.unmodifiableList(sites);
    }

    private void checkSiteConfig() {
        if (sites == null || sites.isEmpty()) {
            throw new RuntimeException("Не задана конфигурация сайтов для индексирования и поиска!");
        }
        if (sites.stream().anyMatch(s -> s.getUrl().isEmpty())) {
            throw new RuntimeException("В конфигурации сайтов для индексирования и поиска есть неопределенное значение!");
        }
    }

    private static void checkPageUrl(String pageUrl) {
        if (pageUrl == null || pageUrl.isEmpty()) {
            String errorMsg = "Страница для индексирования не задана!";
            LOG.error("checkPageUrl: error = {}", errorMsg);
            throw new IndexSiteException(errorMsg);
        }
    }
}
