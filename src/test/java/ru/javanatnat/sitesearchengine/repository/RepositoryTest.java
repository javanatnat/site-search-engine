package ru.javanatnat.sitesearchengine.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.javanatnat.sitesearchengine.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTest {
    @Container
    private static final PostgreSQLContainer<ListsPostgreSQLContainer> postgresqlContainer
            = ListsPostgreSQLContainer.getInstance();

    @Autowired
    IndexRepository indexRepository;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    SiteRepository siteRepository;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    LemmaFrequencyRepository lemmaFrequencyRepository;

    private static Field field1;

    @BeforeEach
    void setUp() {
        List<Field> fields = fieldRepository.findAll();
        Optional<Field> findField = fields.stream().filter(f -> f.getId() == 1L).findFirst();
        assertThat(findField).isPresent();
        field1 = findField.get();
    }

    @Test
    void testIndexBatchInsert() {
        Site site = new Site(
                SiteStatus.INDEXED,
                LocalDateTime.now(),
                null,
                "site.ru",
                "siteName");
        site = siteRepository.customSave(site);

        Optional<Site> findSite = siteRepository.findByUrl("site.ru");
        assertThat(findSite).isPresent();

        Site getSite = findSite.get();
        assertThat(getSite.getLastError()).isNull();
        assertThat(getSite.getName()).isEqualTo("siteName");
        assertThat(getSite.getStatus()).isEqualTo(SiteStatus.INDEXED);
        assertThat(getSite.getStatusTime()).isBefore(LocalDateTime.now());

        Page page = new Page(site.getId(), "/", 200, "tree tree tree");
        page = pageRepository.save(page);

        LemmaFrequency lemma = new LemmaFrequency("leaf", 1);
        LemmaFrequency savedLemma = lemmaFrequencyRepository.save(lemma);

        long lemmaId = savedLemma.getId();

        Index savedIndex = new Index(page.getId(), field1.getId(), lemmaId, BigDecimal.ONE);
        indexRepository.batchInsert(List.of(savedIndex));

        Optional<Index> findIndex = indexRepository.findByPageIdAndLemmaIdAndFieldId(
                page.getId(), lemmaId, field1.getId());
        assertThat(findIndex).isPresent();
        assertThat(findIndex.get()).isEqualTo(savedIndex);

        List<Index> indexes = indexRepository.findByPageIdAndLemmaId(page.getId(), lemmaId);
        assertThat(indexes).containsExactly(savedIndex);
    }

    @Test
    void testFindDistinctPageIdByLemmaId() {
        Site site = new Site(
                SiteStatus.INDEXED,
                LocalDateTime.now(),
                null,
                "siteD.ru",
                "siteName");
        site = siteRepository.customSave(site);

        List<String> pagePaths = List.of("/saved", "/try", "/news", "/home", "/news/3");
        String word = "today";
        LemmaFrequency lemma = new LemmaFrequency(word, 1);
        LemmaFrequency savedLemma = lemmaFrequencyRepository.save(lemma);

        for (String path : pagePaths) {
            Page pageM = new Page(site.getId(), path, 200, word);
            pageM = pageRepository.save(pageM);

            Index indexM = new Index(pageM.getId(), field1.getId(), savedLemma.getId(), BigDecimal.ONE);
            indexRepository.save(indexM);
        }

        List<Long> pageIds = indexRepository.findDistinctPageIdByLemmaId(savedLemma.getId());
        assertThat(pageIds.size()).isEqualTo(pagePaths.size());

        for (Long pageId : pageIds) {
            Optional<Page> findPage = pageRepository.findById(pageId);
            assertThat(findPage).isPresent();
            assertThat(findPage.get().getPath()).isIn(pagePaths);
        }
    }

    @Test
    void testCountDistinctLemmasBySite() {
        Site site = new Site(
                SiteStatus.INDEXED,
                LocalDateTime.now(),
                null,
                "siteM.ru",
                "siteName");
        site = siteRepository.customSave(site);

        List<String> pagePaths = List.of("/saved", "/try", "/news", "/home", "/news/3");
        List<String> words = List.of("love", "trees", "auto");

        Map<String, LemmaFrequency> lemmas = new HashMap<>();

        for (String word : words) {
            LemmaFrequency lemma = new LemmaFrequency(word, 1);
            LemmaFrequency savedLemma = lemmaFrequencyRepository.save(lemma);
            lemmas.put(word, savedLemma);
            Optional<LemmaFrequency> findLemma = lemmaFrequencyRepository.findByLemma(word);
            assertThat(findLemma).isPresent();
            assertThat(findLemma.get()).isEqualTo(lemma);
        }

        for (String path : pagePaths) {
            Page page = new Page(site.getId(), path, 200, "content");
            page = pageRepository.save(page);

            for (String word : words) {
                LemmaFrequency lemma = lemmas.get(word);
                assertThat(lemma).isNotNull();

                Index indexM = new Index(page.getId(), field1.getId(), lemma.getId(), BigDecimal.ONE);
                indexRepository.save(indexM);
            }
        }

        long countDistinctLemmas = indexRepository.countDistinctLemmasBySite(site.getId());
        assertThat(countDistinctLemmas).isEqualTo(words.size());
    }

    @Test
    void testLemmas() {
        List<String> wordsBatch = List.of("toy", "play", "avia");
        lemmaFrequencyRepository.batchInsert(wordsBatch);
        for (String word : wordsBatch) {
            Optional<LemmaFrequency> findLemma = lemmaFrequencyRepository.findByLemma(word);
            assertThat(findLemma).isPresent();
            assertThat(findLemma.get().getFrequency()).isEqualTo(1);
        }

        List<String> words = List.of("tee", "bus", "onion");
        List<LemmaFrequency> savedLemmas = new ArrayList<>();

        for(int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            LemmaFrequency lemmaFrequency = new LemmaFrequency(word, i+1);
            LemmaFrequency savedLemma = lemmaFrequencyRepository.save(lemmaFrequency);
            savedLemmas.add(savedLemma);
        }

        Set<String> param = new HashSet<>(words);

        List<LemmaFrequency> lemmasOrdered = lemmaFrequencyRepository.findAllInSetOrderByFrequencyAsc(param);
        assertThat(lemmasOrdered).containsExactlyElementsOf(savedLemmas);

        List<LemmaFrequency> lemmas = lemmaFrequencyRepository.findAllInSet(param);
        assertThat(lemmas).containsExactlyInAnyOrderElementsOf(savedLemmas);
    }

    @Test
    void testPages() {
        Site site = new Site(
                SiteStatus.INDEXED,
                LocalDateTime.now(),
                null,
                "siteP.ru",
                "siteName");
        site = siteRepository.customSave(site);
        long siteId = site.getId();

        List<Page> savedPages = new ArrayList<>();
        List<String> pagePaths = List.of("/saved", "/try", "/news", "/home", "/news/3");
        for (String path : pagePaths) {
            Page page = new Page(siteId, path, 200, "content");
            page = pageRepository.save(page);
            savedPages.add(page);
        }

        long count = pageRepository.countBySiteId(siteId);
        assertThat(count).isEqualTo(pagePaths.size());

        List<String> words = List.of("crush", "film", "festival");
        lemmaFrequencyRepository.batchInsert(words);

        Map<String, LemmaFrequency> lemmas = new HashMap<>();
        for (String path : pagePaths) {
            Optional<Page> findPage = pageRepository.findBySiteIdAndPath(siteId, path);
            assertThat(findPage).isPresent();
            Page page = findPage.get();

            for (String word : words) {
                Optional<LemmaFrequency> findLemma = lemmaFrequencyRepository.findByLemma(word);
                assertThat(findLemma).isPresent();

                LemmaFrequency lemma = findLemma.get();
                lemmas.put(word, lemma);

                Index index = new Index(page.getId(), field1.getId(), lemma.getId(), BigDecimal.ONE);
                indexRepository.save(index);
            }
        }

        for (String word : words) {
            List<Page> pages = pageRepository.findPagesBySiteAndLemma(siteId, lemmas.get(word).getId());
            assertThat(pages).containsExactlyInAnyOrderElementsOf(savedPages);
        }
    }
}
