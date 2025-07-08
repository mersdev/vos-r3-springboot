package com.vehicleoem.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Base class for integration tests that provides:
 * - Embedded PostgreSQL database using Zonky
 * - WireMock servers for external service mocking
 * - Proper test configuration and cleanup
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",
    "logging.level.com.vehicleoem=DEBUG",
    "feign.device-oem-server.url=http://localhost:9001",
    "feign.kts-server.url=http://localhost:9002",
    "feign.vehicle-telematics.url=http://localhost:9003"
})
public abstract class BaseIntegrationTest {

    private static EmbeddedPostgres embeddedPostgres;
    
    @Autowired
    protected WireMockServer deviceOemMockServer;
    
    @Autowired
    protected WireMockServer ktsMockServer;
    
    @Autowired
    protected WireMockServer telematicsMockServer;

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

        // Configure external service URLs to point to WireMock servers
        registry.add("feign.device-oem-server.url", () -> "http://localhost:9001");
        registry.add("feign.kts-server.url", () -> "http://localhost:9002");
        registry.add("feign.vehicle-telematics.url", () -> "http://localhost:9003");
    }

    /**
     * Reset all WireMock servers to clean state
     */
    protected void resetWireMockServers() {
        deviceOemMockServer.resetAll();
        ktsMockServer.resetAll();
        telematicsMockServer.resetAll();
        
        // Re-setup default stubs
        WireMockConfig config = new WireMockConfig();
        // Note: In a real implementation, you might want to extract stub setup to separate methods
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

    /**
     * Utility method to verify WireMock servers are running
     */
    protected void verifyWireMockServersRunning() {
        assert deviceOemMockServer.isRunning() : "Device OEM mock server is not running";
        assert ktsMockServer.isRunning() : "KTS mock server is not running";
        assert telematicsMockServer.isRunning() : "Telematics mock server is not running";
    }
}
