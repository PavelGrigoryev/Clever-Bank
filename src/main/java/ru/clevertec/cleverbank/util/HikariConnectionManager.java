package ru.clevertec.cleverbank.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@UtilityClass
public class HikariConnectionManager {

    private final HikariConfig CONFIG = new HikariConfig();
    private final HikariDataSource DATA_SOURCE;

    static {
        Map<String, String> postgresqlMap = new YamlUtil().getYamlMap().get("postgresql");
        String url = postgresqlMap.get("url");
        String user = postgresqlMap.get("user");
        String password = postgresqlMap.get("password");
        CONFIG.setJdbcUrl(url);
        CONFIG.setUsername(user);
        CONFIG.setPassword(password);
        CONFIG.setDriverClassName("org.postgresql.Driver");
        CONFIG.setMaximumPoolSize(30);
        DATA_SOURCE = new HikariDataSource(CONFIG);
    }

    public Connection getConnection() {
        Connection connection;
        try {
            connection = DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return connection;
    }

}
