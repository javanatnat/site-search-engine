package ru.javanatnat.sitesearchengine.service.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.sitesearchengine.model.Site;
import ru.javanatnat.sitesearchengine.model.SiteStatus;
import ru.javanatnat.sitesearchengine.parser.TextParser;
import ru.javanatnat.sitesearchengine.service.ContentParser;
import ru.javanatnat.sitesearchengine.service.SiteParam;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class SiteParseTask implements Callable<Void> {
    private static final Logger LOG = LoggerFactory.getLogger(SiteParseTask.class);
    private final SiteParam siteParam;
    private final TextParser textParser;
    private final DbIndexService dbService;

    public SiteParseTask(
            SiteParam siteParam,
            TextParser textParser,
            DbIndexService dbService
    ) {
        this.siteParam = siteParam;
        this.textParser = textParser;
        this.dbService = dbService;
    }

    @Override
    public Void call() throws Exception {
        LOG.info("start run: site = {}", siteParam);
        ContentParser mainPageParser = ContentParser.getInstanceByUrl(getSiteUrl());

        SiteStatus siteStatus = mainPageParser.codeResponseIsOk() ? SiteStatus.INDEXING : SiteStatus.FAILED;
        LOG.debug("siteStatus = {}", siteStatus);
        LOG.debug("code response = {}", mainPageParser.getCodeResponse());

        Site site = new Site(
                siteStatus,
                LocalDateTime.now(),
                mainPageParser.getErrorMessage(),
                getSiteUrl(),
                getSiteName()
        );
        LOG.debug("site = {}", site);

        Site savedSite = dbService.saveSite(site);
        LOG.debug("savedSite = {}", savedSite);

        if (siteStatus == SiteStatus.INDEXING) {
            try {
                PageParseTask mainPageTask = new PageParseTask(
                        getMainPageUrl(),
                        savedSite,
                        textParser,
                        dbService)
                        .setContentParser(mainPageParser);

                mainPageTask.call();
                LOG.info("main page successfully compute");

                Set<String> sameSiteRefs = filterSiteRefs(mainPageParser.getAllContentRefs());
                LOG.debug("sameSiteRefs: {}", sameSiteRefs);

                Set<String> allSiteRefs = new HashSet<>(sameSiteRefs);
                allSiteRefs.add(getMainPageUrl());
                
                Queue<String> toDoRefs = new ConcurrentLinkedQueue<>(sameSiteRefs);
                LOG.debug("allSiteRefs: {}", allSiteRefs.size());

                int i = 0;
                for (String pageRef : toDoRefs) {
                    LOG.info("{}) pageRef: {}", ++i, pageRef);

                    try {
                        ContentParser pageParser = ContentParser.getInstanceByUrl(pageRef);
                        LOG.debug("page code response: {}", pageParser.getCodeResponse());

                        PageParseTask pageTask = new PageParseTask(
                                pageRef,
                                savedSite,
                                textParser,
                                dbService)
                                .setContentParser(pageParser);

                        pageTask.call();
                        LOG.debug("page {} successfully compute", pageRef);

                        if (pageParser.codeResponseIsOk()) {
                            Set<String> pageRefs = filterSiteRefs(pageParser.getAllContentRefs());

                            pageRefs.removeAll(allSiteRefs);
                            LOG.debug("pageRefs: {}", pageRefs);

                            toDoRefs.addAll(pageRefs);
                            allSiteRefs.addAll(pageRefs);
                        }
                    } catch (Exception e) {
                        LOG.error("page = {}, error: {}", pageRef, e);
                    }
                }
                LOG.debug("finish work with site pages");
                saveSuccess(savedSite);
                LOG.debug("success update site indexed info");

            } catch (Exception e) {
                String errorMsg = "Ошибка при индексировании сайта " + getSiteUrl() + " : " + e.getMessage();
                saveFail(savedSite, errorMsg);
                LOG.debug("success update site fail info");
                LOG.error(errorMsg);
                throw new IndexSiteException(errorMsg, e);
            }
        }
        LOG.info("end run: site = {}", siteParam);
        return null;
    }

    private String getSiteUrl() {
        return siteParam.getUrl();
    }

    private String getSiteName() {
        return siteParam.getName();
    }

    private void saveFail(Site site, String errorMsg) {
        updateSite(site, errorMsg, SiteStatus.FAILED);
    }

    private void saveSuccess(Site site) {
        updateSite(site, null, SiteStatus.INDEXED);
    }

    private void updateSite(Site site, String lastError, SiteStatus status) {
        site.setLastError(lastError);
        site.setStatus(status);
        site.setStatusTime(LocalDateTime.now());
        dbService.saveSite(site);
    }

    private Set<String> filterSiteRefs(Set<String> allPageRefs) {
        String siteUrl = getSiteUrl();
        String mainPageUrl = getMainPageUrl();

        return allPageRefs
                .stream()
                .filter(s -> s.startsWith(siteUrl))
                .filter(s -> !s.equals(mainPageUrl))
                .collect(Collectors.toSet());
    }

    private String getMainPageUrl() {
        return getSiteUrl() + "/";
    }
}
