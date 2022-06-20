package ru.javanatnat.sitesearchengine.service.index;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cache<T> {
    private final List<SoftReference<T>> cache;
    private final Class<T> clazz;
    private int size;

    public Cache(Class<T> clazz, int size) {
        this.cache = new ArrayList<>();
        this.clazz = clazz;
        this.size = size;
    }

    public Cache(Class<T> clazz) {
        this.cache = new ArrayList<>();
        this.clazz = clazz;
        this.size = 0;
    }

    public boolean cacheIsNotActual() {
        return cache.isEmpty()
                || (size == 0)
                || cache.stream().anyMatch(s -> s.get() == null)
                || (cache.stream().filter(s -> s.get() != null).count() != size);
    }

    public boolean cacheIsActual() {
        return !cacheIsNotActual();
    }

    public void initCache(List<T> elements) {
        checkElementsForCache(elements);
        cache.clear();
        size = elements.size();
        for (T element : elements) {
            cache.add(new SoftReference<>(element));
        }
    }

    public List<T> getElements() {
        if (cacheIsActual()) {
            List<T> result = new ArrayList<>();
            for (SoftReference<T> ref : cache) {
                T element = ref.get();
                if (element != null) {
                    result.add(element);
                } else {
                    break;
                }
            }
            if (result.size() == size) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    public int size() {
        return size;
    }

    private void checkElementsForCache(List<T> elements) {
        if (elements == null
                || elements.isEmpty()
                || elements.stream().anyMatch(Objects::isNull)) {
            throw new RuntimeException("cache (" + clazz.getSimpleName() + ") needs only non null elements!");
        }
    }
}
