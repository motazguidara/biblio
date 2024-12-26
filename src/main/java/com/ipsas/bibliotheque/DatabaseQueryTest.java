package com.ipsas.bibliotheque;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseQueryTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseQueryTest.class.getName());

    public static void main(String[] args) {
        try {
            // Obtenir une connexion à la base de données
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            
            // Tester la connexion
            if (!dbConnection.testConnection()) {
                LOGGER.severe("Impossible de se connecter à la base de données.");
                return;
            }

            // Récupérer une connexion
            try (Connection conn = dbConnection.getConnection()) {
                // Requête pour vérifier les données existantes
                String query = "SELECT id, titre, auteur, isbn, annee_publication, disponible FROM ouvrage";
                
                try (PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    LOGGER.info("Contenu de la table 'ouvrage':");
                    int count = 0;
                    
                    while (rs.next()) {
                        count++;
                        LOGGER.info(String.format(
                            "Livre #%d: ID=%d, Titre='%s', Auteur='%s', ISBN='%s', Année=%d, Disponible=%b", 
                            count,
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("auteur"),
                            rs.getString("isbn"),
                            rs.getInt("annee_publication"),
                            rs.getBoolean("disponible")
                        ));
                    }
                    
                    if (count == 0) {
                        LOGGER.warning("Aucun livre trouvé dans la base de données.");
                    } else {
                        LOGGER.info(count + " livre(s) trouvé(s) dans la base de données.");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'accès à la base de données", e);
        }
    }
}
