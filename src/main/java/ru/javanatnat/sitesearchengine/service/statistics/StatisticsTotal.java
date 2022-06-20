package ru.javanatnat.sitesearchengine.service.statistics;

public class StatisticsTotal {
    private final long sites;
    private final long pages;
    private final long lemmas;
    private final boolean isIndexing;

    public StatisticsTotal(
            long sites,
            long pages,
            long lemmas,
            boolean isIndexing
    ) {
        this.sites = sites;
        this.pages = pages;
        this.lemmas = lemmas;
        this.isIndexing = isIndexing;
    }

    public long getSites() {
        return sites;
    }

    public long getPages() {
        return pages;
    }

    public long getLemmas() {
        return lemmas;
    }

    public boolean isIndexing() {
        return isIndexing;
    }

    @Override
    public String toString() {
        return "StatisticsTotal{" +
                "sites=" + sites +
                ", pages=" + pages +
                ", lemmas=" + lemmas +
                ", isIndexing=" + isIndexing +
                '}';
    }
}
