package ru.javanatnat.sitesearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("t_page")
public class Page {
    @Id
    private final Long id;
    private final Long siteId;
    private final String path;
    private final Integer codeResponse;
    private final String content;

    @PersistenceConstructor
    public Page(Long id, Long siteId, String path, Integer codeResponse, String content) {
        this.id = id;
        this.siteId = siteId;
        this.path = path;
        this.codeResponse = codeResponse;
        this.content = content;
    }

    public Page(Long siteId, String path, Integer codeResponse, String content) {
        this(null, siteId, path, codeResponse, content);
    }

    public Long getId() {
        return id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getPath() {
        return path;
    }

    public Integer getCodeResponse() {
        return codeResponse;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", path='" + path + '\'' +
                ", codeResponse=" + codeResponse +
                ", content='" + content.substring(0, 100) + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof Page page)) {
            return false;
        }

        if (!siteId.equals(page.siteId)) {
            return false;
        }

        return path.equals(page.path);
    }

    @Override
    public int hashCode() {
        int result = siteId.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
