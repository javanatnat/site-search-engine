package ru.javanatnat.sitesearchengine.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javanatnat.sitesearchengine.service.index.IndexSiteException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ContentParser {
    private static final Logger LOG = LoggerFactory.getLogger(ContentParser.class);

    static final String REFERRER = "https://www.google.com/";

    protected final Document document;
    protected final int codeResponse;
    protected final String errorMessage;
    protected final String url;

    protected ContentParser(
            Document document,
            int codeResponse
    ) {
        this(document, codeResponse, null, null);
    }

    protected ContentParser(
            Document document,
            int codeResponse,
            String errorMessage,
            String url
    ) {
        this.document = document;
        this.codeResponse = codeResponse;
        this.errorMessage = errorMessage;
        this.url = url;
    }

    public static ContentParser getInstanceByContent(String content) {
        return new ContentDocumentParser(
                Jsoup.parse(content)
        );
    }

    public static ContentParser getInstanceByUrl(String url) {
        try {
            Connection.Response response = getResponse(url);
            int codeResponse = response.statusCode();
            LOG.debug("url = {}, codeResponse = {}", url, codeResponse);
            if (codeResponseIsOk(codeResponse)) {
                return new ContentDocumentParser(
                        response.parse(),
                        url);
            } else {
                return new ContentEmptyParser(
                        codeResponse,
                        getErrorMessage(response),
                        url
                );
            }
        } catch (IOException ex) {
            throw new IndexSiteException("Ошибка при чтении текста страницы " + url, ex);
        }
    }

    public abstract String getTitle();

    public abstract String getBodyText();

    public abstract String getSnippet(Set<String> queryWords);

    public abstract String getContent();

    public abstract int getCodeResponse();

    public abstract String getErrorMessage();

    public abstract Set<String> getAllContentRefs();

    public abstract String getElementText(String selector);

    public abstract boolean codeResponseIsOk();

    private static Connection.Response getResponse(String url) throws IOException {
        return Jsoup.connect(url).
                referrer(REFERRER).
                followRedirects(false).
                maxBodySize(0).
                execute();
    }

    private static String getErrorMessage(Connection.Response response) {
        if (!codeResponseIsOk(response.statusCode())) {
            return response.statusMessage();
        }
        return null;
    }

    private static boolean codeResponseIsOk(int codeResponse) {
        return (codeResponse == HttpURLConnection.HTTP_OK);
    }
}
