package ru.javanatnat.sitesearchengine.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.javanatnat.sitesearchengine.model.Field;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FieldRepositoryTest {
    @Container
    private static final PostgreSQLContainer<ListsPostgreSQLContainer> postgresqlContainer
            = ListsPostgreSQLContainer.getInstance();

    @Autowired
    FieldRepository fieldRepository;

    @Test
    void testSimple() {
        assertThat(postgresqlContainer.isRunning()).isTrue();
    }

    @Test
    void testFindAll() {
        List<Field> fields = fieldRepository.findAll();
        assertThat(fields).containsExactlyInAnyOrder(
                new Field("title", "title", BigDecimal.valueOf(1.0)),
                new Field("body", "body", BigDecimal.valueOf(0.8))
        );
    }
}
