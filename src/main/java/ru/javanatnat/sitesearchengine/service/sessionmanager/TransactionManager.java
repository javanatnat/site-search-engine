package ru.javanatnat.sitesearchengine.service.sessionmanager;

public interface TransactionManager {
    <T> T doInTransaction(TransactionAction<T> action);
}
