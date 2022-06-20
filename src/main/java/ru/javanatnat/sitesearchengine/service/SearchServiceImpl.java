package ru.javanatnat.sitesearchengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.javanatnat.sitesearchengine.model.*;
import ru.javanatnat.sitesearchengine.parser.TextParser;
import ru.javanatnat.sitesearchengine.service.search.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService{
    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final TextParser parser;
    private final DbSearchService dbService;

    public SearchServiceImpl(
            TextParser parser,
            DbSearchService dbService
    ) {
        this.parser = parser;
        this.dbService = dbService;
    }

    @Override
    public SearchResponse search(SearchRequest request) {
        LOG.info("request: {}", request);
        try {
            List<Site> sites = getSites(request);

            Map<String, List<String>> queryWordsForms = parser.getWordsForms(request.getQuery());
            LOG.debug("queryWordsForms: {}", queryWordsForms);
            Set<String> querySourceLemmas = queryWordsForms.keySet();

            List<String> queryWords = queryWordsForms.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .toList();
            LOG.debug("queryWords: {}", queryWords);

            Set<String> lemmas = queryWordsForms.keySet();
            LOG.debug("lemmas: {}", lemmas);

            List<LemmaFrequency> lemmaFrequencyList = dbService.getLemmasBySetValues(lemmas);
            LOG.debug("lemmaFrequencyList: {}", lemmaFrequencyList);

            if (!lemmaFrequencyList.isEmpty()) {

                for(Site site : sites) {
                    LOG.debug("query data for site: {}", site);

                    LemmaFrequency firstLemma = lemmaFrequencyList.get(0);
                    List<Page> pages = dbService.getPages(site, firstLemma);
                    LOG.debug("pages: {}", pages.size());

                    int idx = 1;
                    while (idx < lemmaFrequencyList.size() && !pages.isEmpty()) {
                        LemmaFrequency lemma = lemmaFrequencyList.get(idx);
                        pages.retainAll(dbService.getPages(site, lemma));
                        LOG.debug("pages: {}", pages.size());
                        idx++;
                    }

                    if (pages.isEmpty()) {
                        continue;
                    }

                    SearchResponse searchResponse = new SearchResponse();

                    for (Page page : pages) {
                        LOG.debug("page: {}", page);

                        ContentParser contentParser = ContentParser.getInstanceByContent(page.getContent());
                        BigDecimal relevance = BigDecimal.valueOf(0.0);
                        Set<Index> indexes = new HashSet<>();

                        for (LemmaFrequency lemmaFrequency : lemmaFrequencyList) {
                            LOG.debug("lemmaFrequency: {}", lemmaFrequency);
                            indexes.addAll(dbService.getIndexByPageAndLemma(page, lemmaFrequency));
                            LOG.debug("index: {}", indexes);
                        }

                        if (!indexes.isEmpty()) {
                            relevance = relevance.add(indexes.stream()
                                    .map(Index::getIndexRank)
                                    .reduce(BigDecimal::add)
                                    .get());
                        }
                        LOG.debug("relevance: {}", relevance);

                        Set<String> wordsForSnippet = new HashSet<>(queryWordsForms.keySet());
                        wordsForSnippet.addAll(queryWords);
                        LOG.debug("wordsForSnippet: {}", wordsForSnippet);

                        Map<String, List<String>> titleData = parser.getWordsForms(contentParser.getTitle());
                        titleData.keySet().removeIf(tl -> !(querySourceLemmas.contains(tl)));
                        LOG.debug("titleData: {}", titleData);
                        if (!titleData.isEmpty()) {
                            wordsForSnippet.addAll(
                                    titleData.values()
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .collect(Collectors.toSet()));
                        }

                        Map<String, List<String>> bodyData = parser.getWordsForms(contentParser.getBodyText());
                        bodyData.keySet().removeIf(bl -> !(querySourceLemmas.contains(bl)));
                        LOG.debug("bodyData: {}", bodyData);
                        if (!bodyData.isEmpty()) {
                            wordsForSnippet.addAll(
                                    bodyData.values()
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .collect(Collectors.toSet()));
                        }
                        LOG.debug("enrich wordsForSnippet: {}", wordsForSnippet);

                        SearchData searchData = new SearchData.Builder(site.getUrl())
                                .setSiteName(site.getName())
                                .setUri(page.getPath())
                                .setRelevance(relevance.doubleValue())
                                .setTitle(contentParser.getTitle())
                                .setSnippet(contentParser.getSnippet(wordsForSnippet))
                                .build();

                        LOG.debug("searchData: {}", searchData);
                        searchResponse.addData(searchData);
                    }
                    LOG.debug("searchResponse: {}", searchResponse);

                    recalcResponse(searchResponse);
                    searchResponse.cutDataList(
                            request.getLimit(),
                            request.getOffset()
                    );
                    return searchResponse;
                }
            }
        } catch (Exception e) {
            String errorMsg = "Ошибка поиска данных, поисковый запрос: " + request;
            LOG.error(errorMsg);
            throw new SearchSiteException(errorMsg, e);
        }
        LOG.debug("return empty response for query = {}", request);
        return SearchResponse.getEmpty();
    }

    private List<Site> getSites(SearchRequest request) {
        String siteUrl = request.getSite();
        LOG.debug("getSites: siteUrl from request = {}", siteUrl);

        if (siteUrl == null || siteUrl.isEmpty()) {
            List<Site> sites = dbService.getIndexedSites();

            if (sites.isEmpty()) {
                String errorMsg = "Не найдены индексированные сайты! " +
                        "Для успешного поиска необходимо запустить процесс индексации сайтов!";
                LOG.error(errorMsg);
                throw new SearchSiteException(errorMsg);
            }
            LOG.debug("getSites return: {}", sites);
            return sites;
        }

        Optional<Site> findSite = dbService.getSite(siteUrl);
        if (findSite.isPresent()) {
            Site site = findSite.get();
            if (site.siteIsIndexed()) {
                LOG.debug("getSites return: {}", site);
                return List.of(site);
            }
        }

        String errorMsg = "Не найден индексированный сайт для значения url = " + siteUrl;
        LOG.error(errorMsg);
        throw new SearchSiteException(errorMsg);
    }

    private void recalcResponse(SearchResponse searchResponse) {
        var data = searchResponse.getDataList();
        searchResponse.setResult(!data.isEmpty());
        searchResponse.setCount(data.size());

        if (!data.isEmpty()) {
            double maxRelevance = data
                    .stream()
                    .map(SearchData::getRelevance)
                    .max(Double::compareTo)
                    .get();

            LOG.debug("maxRelevance: {}", maxRelevance);
            data.forEach( d -> {
                BigDecimal curR = BigDecimal.valueOf(d.getRelevance());
                BigDecimal maxR = BigDecimal.valueOf(maxRelevance);
                d.setRelevance(curR.divide(maxR, RoundingMode.CEILING).doubleValue());
            });

            data.sort(Comparator.comparingDouble(SearchData::getRelevance).reversed());
        }

        LOG.debug("after recalc return searchResponse: {}", searchResponse);
    }
}
