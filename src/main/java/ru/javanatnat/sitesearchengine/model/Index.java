package ru.javanatnat.sitesearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("t_index")
public class Index {
    @Id
    private final Long id;
    private final Long pageId;
    private final Long fieldId;
    private final Long lemmaId;
    private BigDecimal indexRank;

    @PersistenceConstructor
    public Index(Long id, Long pageId, Long fieldId, Long lemmaId, BigDecimal indexRank) {
        this.id = id;
        this.pageId = pageId;
        this.fieldId = fieldId;
        this.lemmaId = lemmaId;
        this.indexRank = indexRank;
    }

    public Index(Long pageId, Long fieldId, Long lemmaId, BigDecimal indexRank) {
        this(null, pageId, fieldId, lemmaId, indexRank);
    }

    public Long getId() {
        return id;
    }

    public Long getPageId() {
        return pageId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public Long getLemmaId() {
        return lemmaId;
    }

    public BigDecimal getIndexRank() {
        return indexRank;
    }

    public void setIndexRank(BigDecimal indexRank) {
        this.indexRank = indexRank;
    }

    @Override
    public String toString() {
        return "Index{" +
                "id=" + id +
                ", pageId=" + pageId +
                ", fieldId=" + fieldId +
                ", lemmaId=" + lemmaId +
                ", indexRank=" + indexRank +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof Index index)) {
            return false;
        }

        if (!pageId.equals(index.pageId)) {
            return false;
        }

        if (!fieldId.equals(index.fieldId)) {
            return false;
        }

        return lemmaId.equals(index.lemmaId);
    }

    @Override
    public int hashCode() {
        int result = pageId.hashCode();
        result = 31 * result + fieldId.hashCode();
        result = 31 * result + lemmaId.hashCode();
        return result;
    }
}
