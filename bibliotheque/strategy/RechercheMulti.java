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

public class RechercheMulti implements RechercheStrategy {
    private static final Logger LOGGER = Logger.getLogger(RechercheMulti.class.getName());

    @Override
    public List<Ouvrage> rechercher(Map<String, Object> criteres) {
        List<Ouvrage> resultats = new ArrayList<>();
        
        // Log the search criteria
        LOGGER.info("Critères de recherche multi-critères: " + criteres);
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            StringBuilder queryBuilder = new StringBuilder("SELECT id, titre, auteur, isbn, annee_publication, disponible FROM ouvrage WHERE 1=1 ");
            List<Object> params = new ArrayList<>();
            
            // Critères de recherche possibles
            if (criteres.containsKey("motCle")) {
                String motCle = (String) criteres.get("motCle");
                queryBuilder.append("AND (titre LIKE ? OR auteur LIKE ?) ");
                params.add("%" + motCle + "%");
                params.add("%" + motCle + "%");
            }
            
            if (criteres.containsKey("auteur")) {
                String auteur = (String) criteres.get("auteur");
                queryBuilder.append("AND auteur LIKE ? ");
                params.add("%" + auteur + "%");
            }
            
            if (criteres.containsKey("anneePublication")) {
                Integer annee = (Integer) criteres.get("anneePublication");
                queryBuilder.append("AND annee_publication = ? ");
                params.add(annee);
            }
            
            // Log the constructed query
            LOGGER.info("Requête de recherche multi-critères: " + queryBuilder.toString());
            
            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                // Remplir les paramètres
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    // Log the ResultSet
                    LOGGER.info("Exécution de la requête de recherche multi-critères.");
                    
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
                        LOGGER.info("Résultat de recherche multi-critères: " + ouvrage);
                    }
                    
                    LOGGER.info("Nombre total de résultats de recherche multi-critères: " + resultats.size());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche multi-critères", e);
        }
        
        return resultats;
    }
}
