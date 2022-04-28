package ru.javanatnat.sitesearchengine.luceneanalyze;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;

public abstract class CustomLangAnalyzer extends StopwordAnalyzerBase {
    public CustomLangAnalyzer(final CharArraySet stopwords) {
        super(stopwords);
    }
}
