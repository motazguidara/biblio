package com.ipsas.bibliotheque.strategy;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.LivrePapier;
import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RechercheMono implements RechercheStrategy {
    private static final Logger LOGGER = Logger.getLogger(RechercheMono.class.getName());

    @Override
    public List<Ouvrage> rechercher(Map<String, Object> criteres) {
        List<Ouvrage> resultats = new ArrayList<>();
        
        // Vérifier si des critères sont fournis
        if (criteres == null || criteres.isEmpty()) {
            LOGGER.warning("Aucun critère de recherche fourni.");
            return resultats;
        }
        
        // Construire dynamiquement la requête SQL
        StringBuilder queryBuilder = new StringBuilder("SELECT id, titre, auteur, isbn, annee, editeur, disponible FROM ouvrage WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Ajouter les critères de recherche
        if (criteres.containsKey("titre")) {
            queryBuilder.append(" AND titre LIKE ?");
            String titre = (String) criteres.get("titre");
            params.add("%" + titre + "%");
        }
        
        if (criteres.containsKey("anneePublication")) {
            queryBuilder.append(" AND annee = ?");
            params.add(criteres.get("anneePublication"));
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            
            // Définir les paramètres de recherche
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Traiter les résultats
                while (rs.next()) {
                    LivrePapier ouvrage = new LivrePapier();
                    ouvrage.setId(rs.getString("id"));
                    ouvrage.setTitre(rs.getString("titre"));
                    ouvrage.setAuteur(rs.getString("auteur"));
                    ouvrage.setIsbn(rs.getString("isbn"));
                    ouvrage.setEditeur(rs.getString("editeur"));
                    ouvrage.setType(Ouvrage.Type.LIVRE);
                    ouvrage.setEstNumerique(false);
                    
                    resultats.add(ouvrage);
                }
                
                // Journaliser le nombre de résultats
                LOGGER.info("Recherche mono-critère terminée. Nombre de résultats : " + resultats.size());
            } catch (SQLException e) {
                // Journaliser les erreurs liées à l'exécution de la requête
                LOGGER.log(Level.SEVERE, "Erreur lors de l'exécution de la requête de recherche", e);
            }
            
        } catch (SQLException e) {
            // Journaliser l'erreur de connexion ou de préparation de la requête
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche mono-critère", e);
        }
        
        return resultats;
    }
}
