package com.vehicleoem.config;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Base class for unit tests that provides:
 * - Embedded PostgreSQL database using Zonky
 * - Proper test configuration and cleanup
 * - No external service mocking (use BaseIntegrationTest for that)
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "logging.level.com.vehicleoem=DEBUG"
})
public abstract class BaseTest {

    private static EmbeddedPostgres embeddedPostgres;

    @BeforeAll
    static void setUpDatabase() throws IOException {
        // Start embedded PostgreSQL
        embeddedPostgres = EmbeddedPostgres.builder()
                .setPort(0) // Use random available port
                .start();
    }

    @AfterAll
    static void tearDownDatabase() throws IOException {
        if (embeddedPostgres != null) {
            embeddedPostgres.close();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure database connection
        registry.add("spring.datasource.url", () -> embeddedPostgres.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        
        // Configure JPA properties
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    /**
     * Get the embedded PostgreSQL DataSource for direct database operations if needed
     */
    protected DataSource getEmbeddedDataSource() {
        return embeddedPostgres.getPostgresDatabase();
    }

    /**
     * Get the JDBC URL of the embedded PostgreSQL instance
     */
    protected String getEmbeddedJdbcUrl() {
        return embeddedPostgres.getJdbcUrl("postgres", "postgres");
    }
}
