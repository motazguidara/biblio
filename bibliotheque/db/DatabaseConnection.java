package com.ipsas.bibliotheque.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static volatile DatabaseConnection instance; // Volatile for double-checked locking
    private HikariDataSource dataSource;

    // Configuration for XAMPP MySQL by default
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bibliotheque?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = System.getenv("DB_USER"); // Fetch from environment variables
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD"); // Fetch from environment variables

    private DatabaseConnection() {
        try {
            // HikariCP Configuration
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Pool configuration
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(60000); // 1 minute
            config.setMaxLifetime(1800000); // 30 minutes
            config.setConnectionTimeout(30000); // 30 seconds
            config.setValidationTimeout(3000); // 3 seconds
            config.setConnectionTestQuery("SELECT 1");

            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP connection pool initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing the connection pool", e);
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            LOGGER.info("Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while connecting to the database", e);
            throw e;
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed successfully");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error while closing the database connection", e);
            }
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "The database is not accessible", e);
            return false;
        }
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            LOGGER.info("HikariCP connection pool shut down");
        }
    }

    // Register a shutdown hook to clean up resources
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.shutdown();
            }
        }));
    }
}
