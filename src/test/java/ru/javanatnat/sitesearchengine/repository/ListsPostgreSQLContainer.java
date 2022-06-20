package ru.javanatnat.sitesearchengine.repository;

import org.testcontainers.containers.PostgreSQLContainer;

public class ListsPostgreSQLContainer extends PostgreSQLContainer<ListsPostgreSQLContainer> {
    private static final String IMAGE_VERSION = "postgres:12";
    private static final String DB_NAME       = "testDB";
    private static final String DB_USERNAME   = "test";
    private static final String DB_PASSWORD   = "test";

    private static ListsPostgreSQLContainer container;

    private ListsPostgreSQLContainer() {
        super(IMAGE_VERSION);
        this.withDatabaseName(DB_NAME)
                .withUsername(DB_USERNAME)
                .withPassword(DB_PASSWORD);
    }

    public static ListsPostgreSQLContainer getInstance() {
        if (container == null) {
            container = new ListsPostgreSQLContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();

        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
