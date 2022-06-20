package ru.javanatnat.sitesearchengine.repository;

import ru.javanatnat.sitesearchengine.model.Index;

import java.util.List;

public interface CustomIndexRepository {
    void batchInsert(List<Index> index);
}
