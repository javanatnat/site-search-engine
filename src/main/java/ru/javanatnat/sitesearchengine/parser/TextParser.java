package ru.javanatnat.sitesearchengine.parser;

import org.springframework.stereotype.Component;
import ru.javanatnat.sitesearchengine.luceneanalyze.CustomEnglishAnalyzer;
import ru.javanatnat.sitesearchengine.luceneanalyze.CustomRussianAnalyzer;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TextParser {
    private final List<LangParser> parsers;

    public TextParser() {
        parsers = List.of(LangParsersHolder.RU_PARSER, LangParsersHolder.ENG_PARSER);
    }

    private static class LangParsersHolder {
        static final LangParser RU_PARSER = new LangParser(new CustomRussianAnalyzer());
        static final LangParser ENG_PARSER = new LangParser(new CustomEnglishAnalyzer());
    }

    public Map<String, Integer> getWordsFrequency(String text) {
        return parsers.stream()
                .map(p -> p.getWordsFrequency(text))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1));
    }

    public Map<String, List<String>> getWordsForms(String text) {
        return parsers.stream()
                .map(p -> p.getWordsForms(text))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1));
    }
}
