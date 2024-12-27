package com.ipsas.bibliotheque.service;

import com.ipsas.bibliotheque.model.*;
import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
    private NotificationService notificationService;

    public ReservationService() {
        this.notificationService = new NotificationService();
    }

    public void reserverOuvrage(String membreId, String ouvrageId) throws SQLException {
        // Vérifier si le membre a déjà une réservation active pour cet ouvrage
        if (hasReservationActive(membreId, ouvrageId)) {
            throw new IllegalStateException("Vous avez déjà une réservation active pour cet ouvrage.");
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();
        
        try {
            conn.setAutoCommit(false);

            // Créer la réservation
            String sql = "INSERT INTO reservation (id, membre_id, ouvrage_id, date_reservation, statut) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String reservationId = java.util.UUID.randomUUID().toString();
                LocalDateTime dateReservation = LocalDateTime.now();

                pstmt.setString(1, reservationId);
                pstmt.setString(2, membreId);
                pstmt.setString(3, ouvrageId);
                pstmt.setTimestamp(4, Timestamp.valueOf(dateReservation));
                pstmt.setString(5, "EN_ATTENTE");
                
                pstmt.executeUpdate();
            }

            conn.commit();
            LOGGER.info("Réservation créée avec succès pour le membre " + membreId);
        } catch (SQLException e) {
            conn.rollback();
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la réservation", e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void annulerReservation(String reservationId) throws SQLException {
        String sql = "UPDATE reservation SET statut = 'ANNULEE', date_annulation = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(2, reservationId);
            
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException("Réservation non trouvée: " + reservationId);
            }
            
            LOGGER.info("Réservation annulée avec succès: " + reservationId);
        }
    }

    public List<Reservation> getReservationsEnAttente(String ouvrageId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE ouvrage_id = ? AND statut = 'EN_ATTENTE' ORDER BY date_reservation";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ouvrageId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getString("id"));
                    reservation.setMembreId(rs.getString("membre_id"));
                    reservation.setOuvrageId(rs.getString("ouvrage_id"));
                    reservation.setDateReservation(rs.getTimestamp("date_reservation").toLocalDateTime());
                    reservation.setStatut(Reservation.StatutReservation.EN_ATTENTE);
                    reservations.add(reservation);
                }
            }
        }
        
        return reservations;
    }

    public void notifierProchaineReservation(String ouvrageId) throws SQLException {
        List<Reservation> reservationsEnAttente = getReservationsEnAttente(ouvrageId);
        
        if (!reservationsEnAttente.isEmpty()) {
            Reservation prochaineReservation = reservationsEnAttente.get(0);
            notificationService.notifierDisponibilite(prochaineReservation.getMembreId(), ouvrageId);
            
            // Mettre à jour le statut de la réservation
            String sql = "UPDATE reservation SET statut = 'NOTIFIEE', date_notification = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(2, prochaineReservation.getId());
                
                pstmt.executeUpdate();
            }
        }
    }

    private boolean hasReservationActive(String membreId, String ouvrageId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservation WHERE membre_id = ? AND ouvrage_id = ? AND statut IN ('EN_ATTENTE', 'NOTIFIEE')";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, membreId);
            pstmt.setString(2, ouvrageId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
