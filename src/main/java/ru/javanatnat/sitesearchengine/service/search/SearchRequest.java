package ru.javanatnat.sitesearchengine.service.search;

public class SearchRequest {
    private static final String FIELD_QUERY = "поисковый запрос";
    private static final String FIELD_OFFSET = "сдвиг для постраничного вывода";
    private static final String FIELD_LIMIT = "количество результатов для вывода";
    private String query;
    private String site;
    private int offset;
    private int limit;

    public SearchRequest() {}

    public SearchRequest(
            String query,
            String site,
            int offset,
            int limit
    ) {
        this.query = query;
        this.site = site;
        this.offset = offset;
        this.limit = limit;
        checkParams();
    }

    private void checkParams() {
        checkQuery();
        checkOffset();
        checkLimit();
    }

    private void checkQuery() {
        checkStringField(query, FIELD_QUERY);
    }

    private void checkStringField(String object, String fieldName) {
        if (object == null || object.isEmpty()) {
            throw new SearchSiteException("Не задано значение поля \"" + fieldName + "\"");
        }
    }

    private void checkOffset() {
        checkIntField(offset, FIELD_OFFSET);
    }

    private void checkIntField(int object, String fieldName) {
        if (object < 0) {
            throw new SearchSiteException("Некорректно указано значение поля \"" + fieldName + "\". " +
                    "Значение должно быть больше нуля.");
        }
    }

    private void checkLimit() {
        checkIntField(limit, FIELD_LIMIT);
    }

    public void setQuery(String query) {
        this.query = query;
        checkQuery();
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        checkOffset();
    }

    public void setLimit(int limit) {
        this.limit = limit;
        checkLimit();
    }

    public String getQuery() {
        return query;
    }

    public String getSite() {
        return site;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
                "query='" + query + '\'' +
                ", site='" + site + '\'' +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
