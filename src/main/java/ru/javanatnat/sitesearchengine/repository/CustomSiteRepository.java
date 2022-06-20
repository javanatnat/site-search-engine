package ru.javanatnat.sitesearchengine.repository;

import ru.javanatnat.sitesearchengine.model.Site;

public interface CustomSiteRepository {
    Site customSave(Site site);
}
