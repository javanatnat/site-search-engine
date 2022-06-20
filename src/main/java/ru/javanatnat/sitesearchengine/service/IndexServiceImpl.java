package ru.javanatnat.sitesearchengine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.javanatnat.sitesearchengine.parser.TextParser;
import ru.javanatnat.sitesearchengine.service.index.*;

@Service
public class IndexServiceImpl implements IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexServiceImpl.class);

    private final AtomicBoolean fullIndexIsWorkNow;
    private final SiteConfig siteConfig;
    private final TextParser textParser;
    private final DbIndexService dbService;
    private ForkJoinPool pool;

    public IndexServiceImpl(
            SiteConfig siteConfig,
            TextParser textParser,
            DbIndexService dbService
    ) {
        this.fullIndexIsWorkNow = new AtomicBoolean();
        this.siteConfig = siteConfig;
        this.textParser = textParser;
        this.dbService = dbService;

        setPool();
    }

    @Override
    public void fullIndex() {
        if (fullIndexIsOn()) {
            LOG.error("Indexing has already started");
            throw new IndexRepeatStartException();
        }

        LOG.info("start full indexing");
        if (fullIndexIsWorkNow.compareAndSet(false, true)) {

            LOG.info("start delete all data...");
            dbService.deleteAllData();
            LOG.info("end delete all data");

            List<Callable<Void>> tasks = new ArrayList<>();
            for(SiteParam site : siteConfig.getSites()) {
                LOG.info("add callable task for site: {}", site);
                tasks.add( new SiteParseTask(site, textParser, dbService));
            }

            runSiteTasks(tasks);
        }
        LOG.info("end full indexing");
    }

    @Override
    public void stopIndex() {
        if (fullIndexIsOff()) {
            LOG.error("there is no working indexing");
            throw new IndexStopErrorException();
        }
        LOG.info("start stop indexing");
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
            LOG.info("indexing was shutdown");
        } catch (InterruptedException e) {
            pool.shutdownNow();
            LOG.error("indexing was shutdown now, exception: {}", e.getMessage());
        }
        fullIndexIsWorkNow.compareAndSet(true, false);
        LOG.info("end stop indexing");
    }

    @Override
    public void indexPage(String pageUrl) {
        LOG.info("start indexing page: {}", pageUrl);
        String cleanPageUrl = pageUrl.toLowerCase(Locale.ROOT).trim();
        if (siteConfig.pageInSiteConfig(cleanPageUrl)) {
            LOG.info("page is in site config: {}", siteConfig);

            PageParseTask pageTask = new PageParseTask(
                    cleanPageUrl,
                    textParser,
                    dbService
            );

            LOG.info("start indexing page...");
            try {
                pageTask.call();
            } catch (Exception e) {
                String errorMsg = "Ошибка индексирования страницы: " + pageUrl;
                LOG.error(errorMsg);
                throw new IndexSiteException(errorMsg, e);
            }
            LOG.info("end indexing page: {}", pageUrl);
        }
        LOG.error("PageOutOfBoundsException for page = {}", pageUrl);
        throw new PageOutOfBoundsException();
    }

    private void setPool() {
        if (pool == null || pool.isShutdown()) {
            pool = new ForkJoinPool(4);
        }
    }

    private void runSiteTasks(List<Callable<Void>> tasks) {
        try {
            setPool();
            pool.invokeAll(tasks);
            fullIndexIsWorkNow.compareAndSet(true, false);
        } catch (Exception e) {
            fullIndexIsWorkNow.compareAndSet(true, false);
            String errorMsg = "Ошибка запуска индексирования сайтов: " + e;
            LOG.error(errorMsg);
            throw new IndexSiteException(errorMsg, e);
        }
    }

    private boolean fullIndexIsOn() {
        return fullIndexIsWorkNow.get();
    }

    private boolean fullIndexIsOff() {
        return !fullIndexIsOn();
    }
}
