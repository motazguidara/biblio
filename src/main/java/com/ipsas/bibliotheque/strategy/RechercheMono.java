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
        
        if (criteres == null || criteres.isEmpty()) {
            LOGGER.warning("Aucun critère de recherche fourni.");
            return resultats;
        }
        
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT o.id, o.titre, o.isbn, o.annee, e.nom as editeur_nom, " +
            "GROUP_CONCAT(CONCAT(a.prenom, ' ', a.nom) SEPARATOR ', ') as auteurs, " +
            "CASE WHEN COUNT(ex.id) > 0 THEN TRUE ELSE FALSE END as disponible " +
            "FROM ouvrage o " +
            "LEFT JOIN editeur e ON o.editeur_id = e.id " +
            "LEFT JOIN ouvrage_auteur oa ON o.id = oa.ouvrage_id " +
            "LEFT JOIN auteur a ON oa.auteur_id = a.id " +
            "LEFT JOIN exemplaire ex ON o.id = ex.ouvrage_id AND ex.disponible = TRUE " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (criteres.containsKey("titre")) {
            queryBuilder.append(" AND o.titre LIKE ?");
            params.add("%" + criteres.get("titre") + "%");
        }
        
        if (criteres.containsKey("anneePublication")) {
            queryBuilder.append(" AND o.annee = ?");
            params.add(criteres.get("anneePublication"));
        }
        
        queryBuilder.append(" GROUP BY o.id, o.titre, o.isbn, o.annee, e.nom");
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            LOGGER.info("Executing query: " + queryBuilder.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LivrePapier ouvrage = new LivrePapier();
                    ouvrage.setId(rs.getString("id"));
                    ouvrage.setTitre(rs.getString("titre"));
                    ouvrage.setAuteur(rs.getString("auteurs"));
                    ouvrage.setIsbn(rs.getString("isbn"));
                    ouvrage.setEditeur(rs.getString("editeur_nom"));
                    ouvrage.setAnnee(rs.getInt("annee"));
                    ouvrage.setDisponible(rs.getBoolean("disponible"));
                    ouvrage.setType(Ouvrage.Type.LIVRE);
                    ouvrage.setEstNumerique(false);
                    
                    resultats.add(ouvrage);
                }
                
                LOGGER.info("Recherche mono-critère terminée. Nombre de résultats : " + resultats.size());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche mono-critère", e);
        }
        
        return resultats;
    }
}
