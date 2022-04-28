package ru.javanatnat.sitesearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Table("t_site")
public class Site {
    @Id
    private final Long id;
    private final SiteStatusType status;
    private final LocalDateTime statusTime;
    private final String lastError;
    private final String url;
    private final String name;

    @PersistenceConstructor
    public Site(Long id, SiteStatusType status, LocalDateTime statusTime,
                String lastError, String url, String name) {
        this.id = id;
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
        this.url = url;
        this.name = name;
    }

    public Site(SiteStatusType status, LocalDateTime statusTime,
                String lastError, String url, String name) {
        this(null, status, statusTime, lastError, url, name);
    }

    public Long getId() {
        return id;
    }

    public SiteStatusType getStatus() {
        return status;
    }

    public LocalDateTime getStatusTime() {
        return statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Site{" +
                "id=" + id +
                ", status=" + status +
                ", statusTime=" + statusTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                ", lastError='" + lastError + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof Site site)) {
            return false;
        }

        return url.equals(site.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
