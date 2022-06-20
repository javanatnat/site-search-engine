package ru.javanatnat.sitesearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("t_lemma_frequency")
public class LemmaFrequency {
    @Id
    private final Long id;
    private final String lemma;
    private Integer frequency;

    @PersistenceConstructor
    public LemmaFrequency(Long id, String lemma, Integer frequency) {
        this.id = id;
        this.lemma = lemma;
        this.frequency = frequency;
    }

    public LemmaFrequency(String lemma, Integer frequency) {
        this(null, lemma, frequency);
    }

    public LemmaFrequency(String lemma) {
        this(null, lemma, 1);
    }

    public Long getId() {
        return id;
    }

    public String getLemma() {
        return lemma;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Lemma{" +
                "id=" + id +
                ", lemma='" + lemma + '\'' +
                ", frequency=" + frequency +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof LemmaFrequency lemmaFrequency)) {
            return false;
        }

        return lemma.equals(lemmaFrequency.lemma);
    }

    @Override
    public int hashCode() {
        return lemma.hashCode();
    }
}
