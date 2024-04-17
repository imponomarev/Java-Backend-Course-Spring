package edu.java.scrapper.integrationEnviroment;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Testcontainers
public class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(true);
        POSTGRES.start();

        try {
            runMigrations(POSTGRES);
        } catch (SQLException | FileNotFoundException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c)
        throws SQLException, LiquibaseException, FileNotFoundException {

        Properties properties = new Properties();
        properties.put("user", c.getUsername());
        properties.put("password", c.getPassword());
        Connection connection = DriverManager.getConnection(
            c.getJdbcUrl(),
            properties
        );

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
            new JdbcConnection(connection)
        );

        Path path = new File(".").toPath().toAbsolutePath().resolve("src").resolve("main").resolve("resources").resolve("migrations");

        Liquibase liquibase = new Liquibase(
            "master.xml",
            new DirectoryResourceAccessor(path),
            database
        );
        liquibase.update(new Contexts(), new LabelExpression());
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
