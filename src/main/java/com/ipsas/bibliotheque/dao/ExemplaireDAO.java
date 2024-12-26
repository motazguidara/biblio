package com.ipsas.bibliotheque.dao;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.Exemplaire;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExemplaireDAO {
    private final DatabaseConnection dbConnection;

    public ExemplaireDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Exemplaire save(Exemplaire exemplaire) throws SQLException {
        String sql = "INSERT INTO exemplaire (id, ouvrage_id, etat, disponible) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (exemplaire.getId() == null) {
                exemplaire.setId(UUID.randomUUID().toString());
            }
            
            stmt.setString(1, exemplaire.getId());
            stmt.setString(2, exemplaire.getOuvrageId());
            stmt.setString(3, exemplaire.getEtat().toString());
            stmt.setBoolean(4, exemplaire.isDisponible());
            
            stmt.executeUpdate();
            return exemplaire;
        }
    }

    public Optional<Exemplaire> findById(String id) throws SQLException {
        String sql = "SELECT * FROM exemplaire WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToExemplaire(rs));
            }
            
            return Optional.empty();
        }
    }

    public List<Exemplaire> findByOuvrage(String ouvrageId) throws SQLException {
        String sql = "SELECT * FROM exemplaire WHERE ouvrage_id = ?";
        List<Exemplaire> exemplaires = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ouvrageId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                exemplaires.add(mapResultSetToExemplaire(rs));
            }
            
            return exemplaires;
        }
    }

    public List<Exemplaire> findAll() throws SQLException {
        String sql = "SELECT * FROM exemplaire";
        List<Exemplaire> exemplaires = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                exemplaires.add(mapResultSetToExemplaire(rs));
            }
            
            return exemplaires;
        }
    }

    public void update(Exemplaire exemplaire) throws SQLException {
        String sql = "UPDATE exemplaire SET ouvrage_id = ?, etat = ?, disponible = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, exemplaire.getOuvrageId());
            stmt.setString(2, exemplaire.getEtat().toString());
            stmt.setBoolean(3, exemplaire.isDisponible());
            stmt.setString(4, exemplaire.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM exemplaire WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private Exemplaire mapResultSetToExemplaire(ResultSet rs) throws SQLException {
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(rs.getString("id"));
        exemplaire.setOuvrageId(rs.getString("ouvrage_id"));
        exemplaire.setEtat(Exemplaire.Etat.valueOf(rs.getString("etat")));
        exemplaire.setDisponible(rs.getBoolean("disponible"));
        return exemplaire;
    }
}
