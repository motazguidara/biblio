package com.ipsas.bibliotheque.dao;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.Emprunt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EmpruntDAO {
    private final DatabaseConnection dbConnection;

    public EmpruntDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Emprunt save(Emprunt emprunt) throws SQLException {
        String sql = "INSERT INTO emprunt (id, membre_id, exemplaire_id, date_emprunt, date_retour_prevue, date_retour_effective) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (emprunt.getId() == null) {
                emprunt.setId(UUID.randomUUID().toString());
            }
            
            stmt.setString(1, emprunt.getId());
            stmt.setString(2, emprunt.getMembreId());
            stmt.setString(3, emprunt.getExemplaireId());
            stmt.setTimestamp(4, Timestamp.valueOf(emprunt.getDateEmprunt()));
            stmt.setTimestamp(5, Timestamp.valueOf(emprunt.getDateRetourPrevue()));
            stmt.setTimestamp(6, emprunt.getDateRetourEffective() != null ? 
                            Timestamp.valueOf(emprunt.getDateRetourEffective()) : null);
            
            stmt.executeUpdate();
            return emprunt;
        }
    }

    public Optional<Emprunt> findById(String id) throws SQLException {
        String sql = "SELECT * FROM emprunt WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToEmprunt(rs));
            }
            
            return Optional.empty();
        }
    }

    public List<Emprunt> findByMembre(String membreId) throws SQLException {
        String sql = "SELECT * FROM emprunt WHERE membre_id = ?";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, membreId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
            
            return emprunts;
        }
    }

    public List<Emprunt> findAll() throws SQLException {
        String sql = "SELECT * FROM emprunt";
        List<Emprunt> emprunts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(mapResultSetToEmprunt(rs));
            }
            
            return emprunts;
        }
    }

    public void update(Emprunt emprunt) throws SQLException {
        String sql = "UPDATE emprunt SET membre_id = ?, exemplaire_id = ?, date_emprunt = ?, " +
                    "date_retour_prevue = ?, date_retour_effective = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, emprunt.getMembreId());
            stmt.setString(2, emprunt.getExemplaireId());
            stmt.setTimestamp(3, Timestamp.valueOf(emprunt.getDateEmprunt()));
            stmt.setTimestamp(4, Timestamp.valueOf(emprunt.getDateRetourPrevue()));
            stmt.setTimestamp(5, emprunt.getDateRetourEffective() != null ? 
                            Timestamp.valueOf(emprunt.getDateRetourEffective()) : null);
            stmt.setString(6, emprunt.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM emprunt WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private Emprunt mapResultSetToEmprunt(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getString("id"));
        emprunt.setMembreId(rs.getString("membre_id"));
        emprunt.setExemplaireId(rs.getString("exemplaire_id"));
        emprunt.setDateEmprunt(rs.getTimestamp("date_emprunt").toLocalDateTime());
        emprunt.setDateRetourPrevue(rs.getTimestamp("date_retour_prevue").toLocalDateTime());
        
        Timestamp dateRetourEffective = rs.getTimestamp("date_retour_effective");
        if (dateRetourEffective != null) {
            emprunt.setDateRetourEffective(dateRetourEffective.toLocalDateTime());
        }
        
        return emprunt;
    }
}
