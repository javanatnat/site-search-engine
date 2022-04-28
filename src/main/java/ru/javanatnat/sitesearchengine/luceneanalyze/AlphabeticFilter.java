package ru.javanatnat.sitesearchengine.luceneanalyze;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public final class AlphabeticFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    AlphabeticFilter(TokenStream in) {
        super(in);
    }

    @Override
    // produce only alphabetic characters
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            char[] checkToken = getCopyCharTermBuffer(
                    termAtt.buffer(),
                    termAtt.toString().length()
            );
            termAtt.setEmpty();

            for(char c : checkToken) {
                if (Character.isAlphabetic(c)) {
                    termAtt.append(c);
                }
            }
            return true;
        }
        return false;
    }

    private static char[] getCopyCharTermBuffer(char[] buffer, int bufferLength) {
        char[] dstBuffer = new char[bufferLength];
        System.arraycopy(buffer, 0, dstBuffer, 0, bufferLength);
        return dstBuffer;
    }
}
