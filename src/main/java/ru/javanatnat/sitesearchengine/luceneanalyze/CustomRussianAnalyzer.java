package ru.javanatnat.sitesearchengine.luceneanalyze;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class CustomRussianAnalyzer extends CustomLangAnalyzer {

    private static final String FILTER_REGEX = "[а-яё]++";
    private static final Set<String> EXCLUDE_STOP_WORDS = Set.of("человек", "жизнь");

    public CustomRussianAnalyzer() {
        this(DefaultStopHolder.DEFAULT_STOP_SET);
    }

    public CustomRussianAnalyzer(final CharArraySet stopwords) {
        super(stopwords);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(source);
        result = new AlphabeticFilter(result);
        result = new RegexMatchFilter(result, FILTER_REGEX);
        result = new StopFilter(result, stopwords);
        result = new SnowballFilter(result, new org.tartarus.snowball.ext.RussianStemmer());
        return new TokenStreamComponents(source, result);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }

    private static class DefaultStopHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        static {
            CharArraySet ruDefaultStopSet = RussianAnalyzer.getDefaultStopSet();
            int capacity = ruDefaultStopSet.size() - EXCLUDE_STOP_WORDS.size();
            Set<Object> cashExcludeStopWordsChar = EXCLUDE_STOP_WORDS.stream()
                    .map(String::toCharArray)
                    .collect(Collectors.toSet());

            DEFAULT_STOP_SET = new CharArraySet(capacity, false);
            for(Object x : ruDefaultStopSet) {
                if (x instanceof char[] cx) {
                    boolean isExclude = false;
                    for (var e : cashExcludeStopWordsChar) {
                        if (Arrays.equals(cx, (char[]) e)) {
                            isExclude = true;
                            break;
                        }
                    }
                    if (!isExclude) {
                        DEFAULT_STOP_SET.add(cx);
                    }
                }
            }
        }
    }
}
