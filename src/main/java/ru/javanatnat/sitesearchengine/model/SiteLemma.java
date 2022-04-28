package ru.javanatnat.sitesearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("t_site_lemma")
public class SiteLemma {
    @Id
    private final Long id;
    private final Long siteId;
    private final String lemma;
    private final Integer frequency;

    @PersistenceConstructor
    public SiteLemma(Long id, Long siteId, String lemma, Integer frequency) {
        this.id = id;
        this.siteId = siteId;
        this.lemma = lemma;
        this.frequency = frequency;
    }

    public SiteLemma(Long siteId, String lemma, Integer frequency) {
        this(null, siteId, lemma, frequency);
    }

    public Long getId() {
        return id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getLemma() {
        return lemma;
    }

    public Integer getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "Lemma{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", lemma='" + lemma + '\'' +
                ", frequency=" + frequency +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof SiteLemma siteLemma)) {
            return false;
        }

        if (!siteId.equals(siteLemma.siteId)) {
            return false;
        }

        return lemma.equals(siteLemma.lemma);
    }

    @Override
    public int hashCode() {
        int result = siteId.hashCode();
        result = 31 * result + lemma.hashCode();
        return result;
    }
}
