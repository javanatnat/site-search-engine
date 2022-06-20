package ru.javanatnat.sitesearchengine.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.javanatnat.sitesearchengine.model.Site;

import java.util.List;
import java.util.Optional;

public interface SiteRepository
        extends
        CrudRepository<Site, Long>,
        CustomSiteRepository {
    @NonNull
    @Override
    List<Site> findAll();
    Optional<Site> findByUrl(String url);
}
