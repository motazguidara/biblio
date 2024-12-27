package com.ipsas.bibliotheque.service;

import com.ipsas.bibliotheque.model.*;
import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EmpruntService {
    private static final Logger LOGGER = Logger.getLogger(EmpruntService.class.getName());
    private static final int DUREE_EMPRUNT_JOURS = 3;
    private static final int JOURS_AVANT_NOTIFICATION = 1;
    private NotificationService notificationService;

    public EmpruntService() {
        this.notificationService = new NotificationService();
    }

    public Emprunt emprunterOuvrage(String membreId, String exemplaireId) throws SQLException {
        // Vérifier si l'exemplaire est disponible
        if (!isExemplaireDisponible(exemplaireId)) {
            throw new IllegalStateException("L'exemplaire n'est pas disponible pour l'emprunt.");
        }

        // Vérifier si le membre n'a pas dépassé la limite d'emprunts ou a des retards
        if (hasEmpruntEnRetard(membreId)) {
            throw new IllegalStateException("Le membre a des emprunts en retard.");
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();
        Emprunt emprunt = null;

        try {
            conn.setAutoCommit(false);

            // Créer l'emprunt
            String sql = "INSERT INTO emprunt (id, membre_id, exemplaire_id, date_emprunt, date_retour_prevue, statut) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String empruntId = java.util.UUID.randomUUID().toString();
                LocalDateTime dateEmprunt = LocalDateTime.now();
                LocalDateTime dateRetourPrevue = dateEmprunt.plusDays(DUREE_EMPRUNT_JOURS);

                pstmt.setString(1, empruntId);
                pstmt.setString(2, membreId);
                pstmt.setString(3, exemplaireId);
                pstmt.setTimestamp(4, Timestamp.valueOf(dateEmprunt));
                pstmt.setTimestamp(5, Timestamp.valueOf(dateRetourPrevue));
                pstmt.setString(6, "EN_COURS");
                
                pstmt.executeUpdate();

                // Mettre à jour la disponibilité de l'exemplaire
                updateExemplaireDisponibilite(conn, exemplaireId, false);

                emprunt = new Emprunt();
                emprunt.setId(empruntId);
                emprunt.setMembreId(membreId);
                emprunt.setExemplaireId(exemplaireId);
                emprunt.setDateEmprunt(dateEmprunt);
                emprunt.setDateRetourPrevue(dateRetourPrevue);
                emprunt.setStatut(Emprunt.StatutEmprunt.EN_COURS);
            }

            conn.commit();
            LOGGER.info("Emprunt créé avec succès: " + emprunt.getId());
            
            // Planifier la notification de rappel
            notificationService.planifierRappelRetour(emprunt);

            return emprunt;
        } catch (SQLException e) {
            conn.rollback();
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'emprunt", e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void retournerOuvrage(String empruntId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false);

            // Mettre à jour l'emprunt
            String sql = "UPDATE emprunt SET date_retour_effective = ?, statut = ? WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                LocalDateTime dateRetour = LocalDateTime.now();
                pstmt.setTimestamp(1, Timestamp.valueOf(dateRetour));
                pstmt.setString(2, "RETOURNE");
                pstmt.setString(3, empruntId);
                
                int updated = pstmt.executeUpdate();
                if (updated == 0) {
                    throw new IllegalStateException("Emprunt non trouvé: " + empruntId);
                }

                // Récupérer l'exemplaire_id
                String exemplaireId = getExemplaireIdFromEmprunt(conn, empruntId);
                
                // Mettre à jour la disponibilité de l'exemplaire
                updateExemplaireDisponibilite(conn, exemplaireId, true);
            }

            conn.commit();
            LOGGER.info("Retour effectué avec succès pour l'emprunt: " + empruntId);
            
            // Notifier les membres en attente
            notifierMembresEnAttente(empruntId);
        } catch (SQLException e) {
            conn.rollback();
            LOGGER.log(Level.SEVERE, "Erreur lors du retour de l'ouvrage", e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Emprunt> getEmpruntsEnRetard() throws SQLException {
        List<Emprunt> empruntsEnRetard = new ArrayList<>();
        String sql = "SELECT * FROM emprunt WHERE date_retour_prevue < ? AND date_retour_effective IS NULL";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Emprunt emprunt = new Emprunt();
                    emprunt.setId(rs.getString("id"));
                    emprunt.setMembreId(rs.getString("membre_id"));
                    emprunt.setExemplaireId(rs.getString("exemplaire_id"));
                    emprunt.setDateEmprunt(rs.getTimestamp("date_emprunt").toLocalDateTime());
                    emprunt.setDateRetourPrevue(rs.getTimestamp("date_retour_prevue").toLocalDateTime());
                    emprunt.setStatut(Emprunt.StatutEmprunt.EN_RETARD);
                    empruntsEnRetard.add(emprunt);
                }
            }
        }
        
        return empruntsEnRetard;
    }

    private boolean isExemplaireDisponible(String exemplaireId) throws SQLException {
        String sql = "SELECT disponible FROM exemplaire WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exemplaireId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getBoolean("disponible");
            }
        }
    }

    private boolean hasEmpruntEnRetard(String membreId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprunt WHERE membre_id = ? AND date_retour_prevue < ? AND date_retour_effective IS NULL";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, membreId);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void updateExemplaireDisponibilite(Connection conn, String exemplaireId, boolean disponible) throws SQLException {
        String sql = "UPDATE exemplaire SET disponible = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, disponible);
            pstmt.setString(2, exemplaireId);
            pstmt.executeUpdate();
        }
    }

    private String getExemplaireIdFromEmprunt(Connection conn, String empruntId) throws SQLException {
        String sql = "SELECT exemplaire_id FROM emprunt WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empruntId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("exemplaire_id");
                }
                throw new IllegalStateException("Emprunt non trouvé: " + empruntId);
            }
        }
    }

    private void notifierMembresEnAttente(String empruntId) {
        // Implementation de la notification des membres en attente
        // À implémenter avec le système de réservation
    }
}
