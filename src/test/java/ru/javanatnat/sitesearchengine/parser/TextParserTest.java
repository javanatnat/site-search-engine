package ru.javanatnat.sitesearchengine.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TextParserTest {
    private static final String SIMPLE_TEXT = "text текст";
    private static final String DIFFICULT_TEXT = "текст тексту текста тек'ст тек5ст'' " +
            "77 , text 344 текстtext text57 texts";

    private static final String RU_TEXT = "текст";
    private static final String ENG_TEXT = "text";

    private static TextParser parser;

    @BeforeAll
    static void setUp() {
        parser = new TextParser();
    }

    @Test
    public void parserSimpleTest() {
        var frequency = parser.getWordsFrequency(SIMPLE_TEXT);
        assertThat(frequency.size()).isEqualTo(2);
        assertThat(frequency.get(RU_TEXT)).isEqualTo(1);
        assertThat(frequency.get(ENG_TEXT)).isEqualTo(1);

        var wordsForms = parser.getWordsForms(SIMPLE_TEXT);
        assertThat(wordsForms.size()).isEqualTo(2);
        assertThat(wordsForms.get(RU_TEXT)).isEqualTo(List.of(RU_TEXT));
        assertThat(wordsForms.get(ENG_TEXT)).isEqualTo(List.of(ENG_TEXT));
    }

    @Test
    public void parserFrequencyTest() {
        var frequency = parser.getWordsFrequency(DIFFICULT_TEXT);
        assertThat(frequency.size()).isEqualTo(2);
        assertThat(frequency.get(RU_TEXT)).isEqualTo(5);
        assertThat(frequency.get(ENG_TEXT)).isEqualTo(3);
    }

    @Test
    public void parserWordsTest() {
        var wordsForms = parser.getWordsForms(DIFFICULT_TEXT);
        assertThat(wordsForms.size()).isEqualTo(2);
        assertThat(wordsForms.get(RU_TEXT).size()).isEqualTo(5);
        assertThat(wordsForms.get(RU_TEXT))
                .isEqualTo(List.of("текст", "тексту", "текста", "тек'ст", "тек5ст"));
        assertThat(wordsForms.get(ENG_TEXT).size()).isEqualTo(3);
        assertThat(wordsForms.get(ENG_TEXT)).isEqualTo(List.of("text", "text57", "texts"));
    }
}
