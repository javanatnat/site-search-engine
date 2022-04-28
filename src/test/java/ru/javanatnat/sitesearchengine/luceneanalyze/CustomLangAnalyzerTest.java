package ru.javanatnat.sitesearchengine.luceneanalyze;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomLangAnalyzerTest {
    private static final String FIELD = "field";

    private static final String RU_TEXT = "текст";
    private static final String ENG_TEXT = "text";
    private static final String EMPTY_TEXT = "";

    private static CustomLangAnalyzer ruAnalyzer;
    private static CustomLangAnalyzer engAnalyzer;

    @BeforeAll
    static void setUp() {
        ruAnalyzer = new CustomRussianAnalyzer();
        engAnalyzer = new CustomEnglishAnalyzer();
    }

    @Test
    public void newCustomRussianAnalyzerTest() throws IOException {
        assertThat(ruAnalyzer.getStopwordSet().size()).isGreaterThan(0);

        var tokens = ruAnalyzer.tokenStream(FIELD, ENG_TEXT);
        tokens.reset();
        assertThat(tokens.incrementToken()).isFalse();
        tokens.close();

        tokens = ruAnalyzer.tokenStream(FIELD, EMPTY_TEXT);
        tokens.reset();
        assertThat(tokens.incrementToken()).isFalse();
        tokens.close();

        tokens = ruAnalyzer.tokenStream(FIELD, RU_TEXT);
        tokens.reset();
        assertThat(tokens.incrementToken()).isTrue();
        tokens.close();
    }

    @Test
    public void newCustomEnglishAnalyzerTest() throws IOException {
        assertThat(engAnalyzer.getStopwordSet().size()).isGreaterThan(0);

        var tokens = engAnalyzer.tokenStream(FIELD, ENG_TEXT);
        tokens.reset();
        assertThat(tokens.incrementToken()).isTrue();
        tokens.close();

        tokens = engAnalyzer.tokenStream(FIELD, EMPTY_TEXT);
        tokens.reset();
        assertThat(tokens.incrementToken()).isFalse();
        tokens.close();

        tokens = engAnalyzer.tokenStream(FIELD, RU_TEXT);
        tokens.reset();
        assertThat(tokens.incrementToken()).isFalse();
        tokens.close();
    }
}
