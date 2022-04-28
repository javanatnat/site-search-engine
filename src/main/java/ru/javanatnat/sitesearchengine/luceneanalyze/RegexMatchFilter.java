package ru.javanatnat.sitesearchengine.luceneanalyze;

import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.util.regex.Pattern;

public final class RegexMatchFilter extends FilteringTokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final Pattern pattern;

    RegexMatchFilter(TokenStream in, String regex) {
        super(in);
        this.pattern = Pattern.compile(regex);
    }

    @Override
    // produce source token without modification only if string view is matched by compiled regex pattern
    protected boolean accept() {
        String term = new String(termAtt.buffer(), 0, termAtt.length());
        return pattern.matcher(term).matches();
    }
}
