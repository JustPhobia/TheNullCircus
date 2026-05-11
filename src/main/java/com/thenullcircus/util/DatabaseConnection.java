package com.thenullcircus.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static final HikariDataSource dataSource;

    static {
        logger.info("[DB_INIT] Spinning up the HikariCP connection pool...");
        try {
            Dotenv dotenv = Dotenv.load();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL"));
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASSWORD"));
            config.setMaximumPoolSize(10);

            dataSource = new HikariDataSource(config);
            logger.info("[DB_SUCCESS] Database connection pool established successfully. Max pool size: 10.");
        } catch (Exception e) {
            logger.severe("[DB_FATAL] High Alert: Failed to initialize database pool. Check your .env configuration immediately: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        logger.fine("[DB_ACCESS] Requesting a connection from the pool...");
        return dataSource.getConnection();
    }
}