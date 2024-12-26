package com.ipsas.bibliotheque.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    
    // Configuration pour XAMPP MySQL par défaut
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bibliotheque?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // XAMPP MySQL n'a pas de mot de passe par défaut
    
    private DatabaseConnection() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("Driver MySQL chargé avec succès");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver MySQL non trouvé", e);
            throw new RuntimeException("Driver MySQL non trouvé", e);
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            LOGGER.info("Connexion établie avec succès");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la connexion à la base de données", e);
            throw e;
        }
    }
    
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Connexion fermée avec succès");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Erreur lors de la fermeture de la connexion", e);
            }
        }
    }
    
    // Méthode utilitaire pour tester si la base de données est accessible
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "La base de données n'est pas accessible", e);
            return false;
        }
    }
}
