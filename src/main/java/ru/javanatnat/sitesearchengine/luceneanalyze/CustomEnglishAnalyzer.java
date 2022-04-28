package ru.javanatnat.sitesearchengine.luceneanalyze;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomEnglishAnalyzer extends CustomLangAnalyzer {

    private static final String FILTER_REGEX = "[a-z]++";

    public CustomEnglishAnalyzer() {
        super(EnglishAnalyzer.getDefaultStopSet());
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new EnglishPossessiveFilter(source);
        result = new LowerCaseFilter(result);
        result = new AlphabeticFilter(result);
        result = new RegexMatchFilter(result, FILTER_REGEX);
        result = new StopFilter(result, stopwords);
        result = new PorterStemFilter(result);
        return new TokenStreamComponents(source, result);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }
}
