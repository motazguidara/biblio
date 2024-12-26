package com.ipsas.bibliotheque.strategy;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RechercheMono implements RechercheStrategy {
    private static final Logger LOGGER = Logger.getLogger(RechercheMono.class.getName());

    @Override
    public List<Ouvrage> rechercher(Map<String, Object> criteres) {
        List<Ouvrage> resultats = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Log the search criteria
            LOGGER.info("Critères de recherche mono-critère: " + criteres);

            // Vérifier si le mot-clé est présent
            if (!criteres.containsKey("motCle")) {
                LOGGER.warning("Aucun mot-clé fourni pour la recherche mono-critère.");
                return resultats;
            }

            String motCle = (String) criteres.get("motCle");
            String query = "SELECT id, titre, auteur, isbn, annee_publication, disponible FROM ouvrage WHERE titre LIKE ? OR auteur LIKE ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Log the prepared statement
                LOGGER.info("Requête de recherche: " + query);
                
                stmt.setString(1, "%" + motCle + "%");
                stmt.setString(2, "%" + motCle + "%");
                
                try (ResultSet rs = stmt.executeQuery()) {
                    // Log the ResultSet
                    LOGGER.info("Exécution de la requête de recherche mono-critère.");
                    
                    while (rs.next()) {
                        Ouvrage ouvrage = new Ouvrage();
                        ouvrage.setId(rs.getInt("id"));
                        ouvrage.setTitre(rs.getString("titre"));
                        ouvrage.setAuteur(rs.getString("auteur"));
                        ouvrage.setIsbn(rs.getString("isbn"));
                        ouvrage.setAnneePublication(rs.getInt("annee_publication"));
                        ouvrage.setDisponible(rs.getBoolean("disponible"));
                        
                        resultats.add(ouvrage);
                        
                        // Log each result explicitly
                        LOGGER.info("Résultat de recherche mono-critère: " + ouvrage);
                    }
                    
                    LOGGER.info("Nombre total de résultats de recherche mono-critère: " + resultats.size());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche mono-critère", e);
        }
        
        return resultats;
    }
}
