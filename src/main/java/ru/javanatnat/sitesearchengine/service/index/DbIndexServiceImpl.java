package ru.javanatnat.sitesearchengine.service.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.javanatnat.sitesearchengine.model.*;
import ru.javanatnat.sitesearchengine.repository.*;
import ru.javanatnat.sitesearchengine.service.SiteConfig;
import ru.javanatnat.sitesearchengine.service.sessionmanager.TransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DbIndexServiceImpl implements DbIndexService {
    private static final Logger LOG = LoggerFactory.getLogger(DbIndexServiceImpl.class);
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final FieldRepository fieldRepository;
    private final LemmaFrequencyRepository lemmaFrequencyRepository;
    private final IndexRepository indexRepository;
    private final TransactionManager transactionManager;
    private final SiteConfig siteConfig;

    private final Cache<Field> fieldCache;

    public DbIndexServiceImpl(
            TransactionManager transactionManager,
            SiteConfig siteConfig,
            SiteRepository siteRepository,
            PageRepository pageRepository,
            FieldRepository fieldRepository,
            LemmaFrequencyRepository lemmaFrequencyRepository,
            IndexRepository indexRepository
    ) {
        this.transactionManager = transactionManager;
        this.siteConfig = siteConfig;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.fieldRepository = fieldRepository;
        this.lemmaFrequencyRepository = lemmaFrequencyRepository;
        this.indexRepository = indexRepository;
        this.fieldCache = new Cache<>(Field.class);
    }

    @Override
    public Page savePage(
            String pageUrl,
            Site site,
            int codeResponse,
            String content
    ) {
        String relUrl = getRelativeUrl(pageUrl, site.getUrl());
        Optional<Page> findPage = pageRepository.findBySiteIdAndPath(site.getId(), relUrl);

        Page page;
        if (findPage.isPresent()) {
            LOG.debug("find page = {}", findPage.get());
            page = findPage.get();
            page.setCodeResponse(codeResponse);
            page.setContent(content);
        } else {
            page = new Page(
                    site.getId(),
                    relUrl,
                    codeResponse,
                    content
            );
            LOG.debug("insert page = {}", page);
        }

        return savePage(page);
    }

    @Override
    public Site getSite(String pageUrl) {
        Optional<String> siteUrl = siteConfig.getSiteUrl(pageUrl);

        if (siteUrl.isPresent()) {
            LOG.debug("find site {} for page {}", siteUrl.get(), pageUrl);
            Optional<Site> findSite = siteRepository.findByUrl(siteUrl.get());

            if (findSite.isPresent()) {
                Site site = findSite.get();
                LOG.debug("find site: {}", site);
                checkSiteIsIndexed(site, pageUrl);
                return site;
            }
        }

        String error = "Не найден сайт для индексирования страницы: " + pageUrl;
        LOG.error(error);
        throw new IndexSiteException(error);
    }

    @Override
    public List<Field> getFields() {
        LOG.debug("field cache size = {}", fieldCache.size());
        List<Field> fields = new ArrayList<>();
        if (fieldCache.cacheIsActual()) {
            fields = fieldCache.getElements();
        }
        if (fields.isEmpty()) {
            fields = fieldRepository.findAll();
            LOG.debug("fields: {}", fields);
            fieldCache.initCache(fields);
        }
        return fields;
    }

    @Override
    public void batchInsertLemmas(Set<String> lemmas) {
        transactionManager.doInTransaction(() -> {
            lemmaFrequencyRepository.batchInsert(lemmas.stream().toList());
            LOG.debug("added lemmas: {} ", lemmas.size());
            return null;
        });
    }

    @Override
    public List<LemmaFrequency> getLemmas(Set<String> lemmas) {
        return lemmaFrequencyRepository.findAllInSet(lemmas);
    }

    @Override
    public void batchInsertIndex(List<Index> index) {
        transactionManager.doInTransaction(() -> {
            indexRepository.batchInsert(index);
            LOG.debug("added index: {} ", index.size());
            return null;
        });
    }

    @Override
    public void deleteAllData() {
        transactionManager.doInTransaction(() -> {
            indexRepository.deleteAll();
            LOG.debug("delete all index");
            lemmaFrequencyRepository.deleteAll();
            LOG.debug("delete all lemmas");
            pageRepository.deleteAll();
            LOG.debug("delete all pages");
            siteRepository.deleteAll();
            LOG.debug("delete all sites");
            return true;
        });
    }

    @Override
    public Site saveSite(Site site) {
        return transactionManager.doInTransaction(() -> {
            Site saved = siteRepository.customSave(site);
            LOG.debug("save site: {}", saved);
            return saved;
        });
    }

    private Page savePage(Page page) {
        return transactionManager.doInTransaction(() -> {
            Page saved = pageRepository.save(page);
            LOG.debug("save page: {}", saved);
            return saved;
        });
    }

    private static void checkSiteIsIndexed(Site site, String pageUrl) {
        if (site.siteIsNotIndexed()) {
            String error = "Сайт \""+ site.getName() +
                    "\" (для переиндексирования страницы: " + pageUrl +
                    ") ещё не проиндексирован! " +
                    "Статус сайта = " + site.getStatus() +
                    " от " + site.getStatusTime();
            LOG.error(error);
            throw new IndexSiteException(error);
        }
    }

    private String getRelativeUrl(String pageUrl, String siteUrl) {
        return pageUrl.replace(siteUrl, "");
    }
}
