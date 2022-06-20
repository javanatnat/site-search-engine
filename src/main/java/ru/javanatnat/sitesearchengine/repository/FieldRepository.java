package ru.javanatnat.sitesearchengine.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.javanatnat.sitesearchengine.model.Field;

import java.util.List;

public interface FieldRepository extends CrudRepository<Field, Long> {
    @NonNull
    @Override
    List<Field> findAll();
}
