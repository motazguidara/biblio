package com.ipsas.bibliotheque.dao;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.Ouvrage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OuvrageDAO {
    private final DatabaseConnection dbConnection;

    public OuvrageDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Ouvrage save(Ouvrage ouvrage) throws SQLException {
        String sql = "INSERT INTO ouvrage (id, titre, editeur, type, est_numerique) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (ouvrage.getId() == null) {
                ouvrage.setId(UUID.randomUUID().toString());
            }
            
            stmt.setString(1, ouvrage.getId());
            stmt.setString(2, ouvrage.getTitre());
            stmt.setString(3, ouvrage.getEditeur());
            stmt.setString(4, ouvrage.getType().toString());
            stmt.setBoolean(5, ouvrage.isEstNumerique());
            
            stmt.executeUpdate();
            return ouvrage;
        }
    }

    public Optional<Ouvrage> findById(String id) throws SQLException {
        String sql = "SELECT * FROM ouvrage WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToOuvrage(rs));
            }
            
            return Optional.empty();
        }
    }

    public List<Ouvrage> findAll() throws SQLException {
        String sql = "SELECT * FROM ouvrage";
        List<Ouvrage> ouvrages = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ouvrages.add(mapResultSetToOuvrage(rs));
            }
            
            return ouvrages;
        }
    }

    public void update(Ouvrage ouvrage) throws SQLException {
        String sql = "UPDATE ouvrage SET titre = ?, editeur = ?, type = ?, est_numerique = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ouvrage.getTitre());
            stmt.setString(2, ouvrage.getEditeur());
            stmt.setString(3, ouvrage.getType().toString());
            stmt.setBoolean(4, ouvrage.isEstNumerique());
            stmt.setString(5, ouvrage.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM ouvrage WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private Ouvrage mapResultSetToOuvrage(ResultSet rs) throws SQLException {
        Ouvrage ouvrage = new Ouvrage();
        ouvrage.setId(rs.getString("id"));
        ouvrage.setTitre(rs.getString("titre"));
        ouvrage.setEditeur(rs.getString("editeur"));
        ouvrage.setType(Ouvrage.Type.valueOf(rs.getString("type")));
        ouvrage.setEstNumerique(rs.getBoolean("est_numerique"));
        return ouvrage;
    }
}
