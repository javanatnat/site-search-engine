package ru.javanatnat.sitesearchengine.service.statistics;

import java.time.LocalDateTime;
import java.util.Objects;

public class StatSiteDetails {
    private final String url;
    private final String name;
    private final String status;
    private final LocalDateTime statusTime;
    private final String error;
    private final long pages;
    private final long lemmas;

    private StatSiteDetails(Builder builder) {
        url = builder.url;
        name = builder.name;
        status = builder.status;
        statusTime = builder.statusTime;
        error = builder.error;
        pages = builder.pages;
        lemmas = builder.lemmas;

        Objects.requireNonNull(url);
        Objects.requireNonNull(name);
    }

    public static class Builder {
        private final String url;
        private String name;
        private String status;
        private LocalDateTime statusTime;
        private String error;
        private long pages;
        private long lemmas;

        public Builder(String url) {
            this.url = url;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setStatusTime(LocalDateTime statusTime) {
            this.statusTime = statusTime;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setPages(long pages) {
            this.pages = pages;
            return this;
        }

        public Builder setLemmas(long lemmas) {
            this.lemmas = lemmas;
            return this;
        }

        public StatSiteDetails build() {
            return new StatSiteDetails(this);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStatusTime() {
        return statusTime;
    }

    public String getError() {
        return error;
    }

    public long getPages() {
        return pages;
    }

    public long getLemmas() {
        return lemmas;
    }

    @Override
    public String toString() {
        return "StatSiteDetails{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", statusTime=" + statusTime +
                ", error='" + error + '\'' +
                ", pages=" + pages +
                ", lemmas=" + lemmas +
                '}';
    }
}
