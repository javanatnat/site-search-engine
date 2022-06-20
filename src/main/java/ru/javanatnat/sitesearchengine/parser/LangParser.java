package ru.javanatnat.sitesearchengine.parser;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.*;
import ru.javanatnat.sitesearchengine.luceneanalyze.CustomLangAnalyzer;

import java.io.IOException;
import java.util.*;

public class LangParser {
    private static final String FIELD = "field";
    private static final String WORDS_FREQUENCY_ERROR = "error while getting words frequency";
    private static final String WORDS_FORMS_ERROR = "error while getting source forms of words";
    private static final int ERROR_TEXT_SIZE = 500;
    private static final int INIT_FREQUENCY = 1;

    private final CustomLangAnalyzer analyzer;

    public LangParser(CustomLangAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * @param text source text
     * @return map where key - norm form of word, value - frequency of norm form word in source text
     * @throws ProcessAnalyzerException if token stream from analyzer produce IOException
     *         by reset or incrementToken operations
     */
    public Map<String, Integer> getWordsFrequency(String text) {
        Map<String, Integer> frequency = new HashMap<>();
        try (TokenStream tokenStream = analyzer.tokenStream(FIELD, text)) {
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String word = charTermAttribute.toString();
                frequency.merge(word, INIT_FREQUENCY, Integer::sum);
            }
        } catch (IOException ioException) {
            throw new ProcessAnalyzerException(
                    getErrorMessage(text, WORDS_FREQUENCY_ERROR),
                    ioException
            );
        }
        return frequency;
    }

    /**
     * @param text source text
     * @return map where key - norm form of word, value - list of source words in sort order
     *         like their were in source text
     * @throws ProcessAnalyzerException if token stream from analyzer produce IOException
     *         by reset or incrementToken operations
     */
    public Map<String, List<String>> getWordsForms(String text) {
        Map<String, List<String>> result = new HashMap<>();
        try (TokenStream tokenStream = analyzer.tokenStream(FIELD, text)) {
            OffsetAttribute offset = tokenStream.addAttribute(OffsetAttribute.class);
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String word = charTermAttribute.toString();
                String source = text.substring(offset.startOffset(), offset.endOffset())
                        .toLowerCase(Locale.ROOT);

                result.computeIfAbsent(word, key -> new ArrayList<>()).add(source);
            }
        } catch (IOException ioException) {
            throw new ProcessAnalyzerException(
                    getErrorMessage(text, WORDS_FORMS_ERROR),
                    ioException
            );
        }
        return result;
    }

    private String getErrorMessage(String sourceText, String errorText) {
        return (errorText + analyzer.getClass().getSimpleName() + ", text = "
                + sourceText.substring(0, ERROR_TEXT_SIZE))
                .substring(0, ERROR_TEXT_SIZE);
    }
}
