package ru.javanatnat.sitesearchengine.service.search;

public class SearchData {
    private final String site;
    private final String siteName;
    private final String uri;
    private final String title;
    private final String snippet;
    private double relevance;

    private SearchData(Builder builder) {
        site = builder.siteUrl;
        siteName = builder.siteName;
        uri = builder.uri;
        title = builder.title;
        snippet = builder.snippet;
        relevance = builder.relevance;
    }

    public static class Builder {
        private final String siteUrl;
        private String siteName;
        private String uri;
        private String title;
        private String snippet;
        private double relevance;

        public Builder(String siteUrl) {
            this.siteUrl = siteUrl;
        }

        public Builder setSiteName(String siteName) {
            this.siteName = siteName;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSnippet(String snippet) {
            this.snippet = snippet;
            return this;
        }

        public Builder setRelevance(double relevance) {
            this.relevance = relevance;
            return this;
        }

        public SearchData build() {
            return new SearchData(this);
        }
    }

    @Override
    public String toString() {
        return "SearchData{" +
                "site='" + site + '\'' +
                ", siteName='" + siteName + '\'' +
                ", uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", snippet='" + snippet + '\'' +
                ", relevance=" + relevance +
                '}';
    }

    public String getSite() {
        return site;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
