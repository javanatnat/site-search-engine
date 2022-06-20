package ru.javanatnat.sitesearchengine.service.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.sitesearchengine.model.*;
import ru.javanatnat.sitesearchengine.parser.TextParser;
import ru.javanatnat.sitesearchengine.service.ContentParser;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class PageParseTask {
    private static final Logger LOG = LoggerFactory.getLogger(PageParseTask.class);
    private final String pageUrl;
    private final Site site;
    private final TextParser textParser;
    private final DbIndexService dbService;
    private ContentParser parser;

    public PageParseTask(
            String pageUrl,
            TextParser textParser,
            DbIndexService dbService
    ) {
        this.pageUrl = pageUrl;
        this.site = dbService.getSite(pageUrl);
        this.textParser = textParser;
        this.dbService = dbService;
    }

    public PageParseTask(
            String pageUrl,
            Site site,
            TextParser textParser,
            DbIndexService dbService
    ) {
        this.pageUrl = pageUrl;
        this.site = site;
        this.textParser = textParser;
        this.dbService = dbService;
    }

    public PageParseTask setContentParser(ContentParser parser) {
        Objects.requireNonNull(parser);
        this.parser = parser;
        return this;
    }

   public void call() {
        LOG.debug("start run: page = {}", pageUrl);
        if (parser == null) {
            setContentParser(ContentParser.getInstanceByUrl(pageUrl));
        }

        Page page = dbService.savePage(
                pageUrl,
                site,
                parser.getCodeResponse(),
                parser.getContent()
        );
        LOG.debug("page insert = {}, code response = {}", page, parser.getCodeResponse());

        if (parser.codeResponseIsOk()) {
            for (Field field : dbService.getFields()) {
                LOG.debug("field = {}", field);

                String fieldText = parser.getElementText(field.getSelector());
                LOG.debug("fieldText = {}", fieldText);

                Map<String, Integer> wordsFrequency = textParser.getWordsFrequency(fieldText);
                LOG.debug("wordsFrequency.size = {}", wordsFrequency.size());

                Set<String> lemmaKeys = wordsFrequency.keySet();
                dbService.batchInsertLemmas(lemmaKeys);
                Map<String, LemmaFrequency> lemmas = dbService.getLemmas(lemmaKeys)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        LemmaFrequency::getLemma,
                                        x -> x));
                LOG.debug("insert lemmas: {}", lemmas.size());

                List<Index> indexBatch = new ArrayList<>(wordsFrequency.size());
                for(var entry : wordsFrequency.entrySet()) {
                    String word = entry.getKey();
                    int frequency = entry.getValue();
                    LOG.debug("word = {}, frequency = {}", word, frequency);

                    LemmaFrequency lemmaFrequency = lemmas.get(word);
                    LOG.debug("lemmaFrequency = {}", lemmaFrequency);

                    BigDecimal indexRank = BigDecimal.valueOf(frequency).multiply(field.getWeight());
                    LOG.debug("indexRank = {}", indexRank);

                    indexBatch.add(
                            new Index(
                                    page.getId(),
                                    field.getId(),
                                    lemmaFrequency.getId(),
                                    indexRank
                            ));
                }

                try {
                    dbService.batchInsertIndex(indexBatch);
                    LOG.debug("insert index: {}", indexBatch.size());

                    if (lemmas.size() != indexBatch.size()) {
                        LOG.error(
                                "size of inserted batches don't equals! " +
                                        "index batch size = {}, " +
                                        "lemmas batch size = {}",
                                indexBatch.size(),
                                lemmas.size());
                    }
                } catch (Exception e) {
                    LOG.error("error: {}", e.getMessage());
                }
            }
        }
        LOG.debug("end run: page = {}", pageUrl);
    }
}
