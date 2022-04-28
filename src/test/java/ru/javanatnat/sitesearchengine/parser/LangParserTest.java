package ru.javanatnat.sitesearchengine.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.javanatnat.sitesearchengine.luceneanalyze.CustomEnglishAnalyzer;
import ru.javanatnat.sitesearchengine.luceneanalyze.CustomRussianAnalyzer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LangParserTest {

    private static final String SIMPLE_TEXT = "text текст";
    private static final String DIFFICULT_TEXT = "текст тексту текста тек'ст тек5ст'' " +
            "77 , text 344 текстtext text57 texts tee texture textile";

    private static final String RU_TEXT = "текст";
    private static final String ENG_TEXT = "text";

    private static LangParser ruParser;
    private static LangParser engParser;

    @BeforeAll
    static void setUp() {
        ruParser = new LangParser(new CustomRussianAnalyzer());
        engParser = new LangParser(new CustomEnglishAnalyzer());
    }

    @Test
    public void ruParserSimpleTest() {
        var frequency = ruParser.getWordsFrequency(SIMPLE_TEXT);
        assertThat(frequency.size()).isEqualTo(1);
        assertThat(frequency.get(RU_TEXT)).isEqualTo(1);
        assertThat(frequency.get(ENG_TEXT)).isNull();

        var wordsForms = ruParser.getWordsForms(SIMPLE_TEXT);
        assertThat(wordsForms.size()).isEqualTo(1);
        assertThat(wordsForms.get(RU_TEXT)).isEqualTo(List.of(RU_TEXT));
        assertThat(wordsForms.get(ENG_TEXT)).isNull();
    }

    @Test
    public void engParserSimpleTest() {
        var frequency = engParser.getWordsFrequency(SIMPLE_TEXT);
        assertThat(frequency.size()).isEqualTo(1);
        assertThat(frequency.get(RU_TEXT)).isNull();
        assertThat(frequency.get(ENG_TEXT)).isEqualTo(1);

        var wordsForms = engParser.getWordsForms(SIMPLE_TEXT);
        assertThat(wordsForms.size()).isEqualTo(1);
        assertThat(wordsForms.get(RU_TEXT)).isNull();
        assertThat(wordsForms.get(ENG_TEXT)).isEqualTo(List.of(ENG_TEXT));
    }

    @Test
    public void ruParserFrequencyTest() {
        var frequency = ruParser.getWordsFrequency(DIFFICULT_TEXT);
        assertThat(frequency.size()).isEqualTo(1);
        assertThat(frequency.get(RU_TEXT)).isEqualTo(5);
    }

    @Test
    public void ruParserWordsTest() {
        var wordsForms = ruParser.getWordsForms(DIFFICULT_TEXT);
        assertThat(wordsForms.size()).isEqualTo(1);
        assertThat(wordsForms.get(RU_TEXT).size()).isEqualTo(5);
        assertThat(wordsForms.get(RU_TEXT))
                .isEqualTo(List.of("текст", "тексту", "текста", "тек'ст", "тек5ст"));
    }

    @Test
    public void engParserFrequencyTest() {
        var frequency = engParser.getWordsFrequency(DIFFICULT_TEXT);
        assertThat(frequency.size()).isEqualTo(4);
        assertThat(frequency.get(ENG_TEXT)).isEqualTo(3);
    }

    @Test
    public void engParserWordsTest() {
        var wordsForms = engParser.getWordsForms(DIFFICULT_TEXT);
        assertThat(wordsForms.size()).isEqualTo(4);
        assertThat(wordsForms.get(ENG_TEXT).size()).isEqualTo(3);
        assertThat(wordsForms.get(ENG_TEXT)).isEqualTo(List.of("text", "text57", "texts"));
    }
}
