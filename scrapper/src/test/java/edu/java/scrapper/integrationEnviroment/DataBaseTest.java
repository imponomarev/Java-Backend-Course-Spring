package edu.java.scrapper.integrationEnviroment;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataBaseTest extends IntegrationTest {

    @Test
    public void dataBaseTest() throws SQLException {
        Properties properties = new Properties();
        properties.put("user", POSTGRES.getUsername());
        properties.put("password", POSTGRES.getUsername());
        Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            properties
        );

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(
            null, null, null,  new String[]{"TABLE"});

        List<String> tables = new ArrayList<>();

        while (resultSet.next()) {
            tables.add(resultSet.getString("TABLE_NAME"));
        }

        Assertions.assertEquals(tables,
            List.of("chat", "chat_link_association", "link", "databasechangelog", "databasechangeloglock"));
    }

}
