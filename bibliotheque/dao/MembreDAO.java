package com.ipsas.bibliotheque.dao;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.Membre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MembreDAO {
    private final DatabaseConnection dbConnection;

    public MembreDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Membre save(Membre membre) throws SQLException {
        String sql = "INSERT INTO membre (id, nom, prenom, email, telephone) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (membre.getId() == null) {
                membre.setId(UUID.randomUUID().toString());
            }
            
            stmt.setString(1, membre.getId());
            stmt.setString(2, membre.getNom());
            stmt.setString(3, membre.getPrenom());
            stmt.setString(4, membre.getEmail());
            stmt.setString(5, membre.getTelephone());
            
            stmt.executeUpdate();
            return membre;
        }
    }

    public Optional<Membre> findById(String id) throws SQLException {
        String sql = "SELECT * FROM membre WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToMembre(rs));
            }
            
            return Optional.empty();
        }
    }

    public List<Membre> findAll() throws SQLException {
        String sql = "SELECT * FROM membre";
        List<Membre> membres = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }
            
            return membres;
        }
    }

    public void update(Membre membre) throws SQLException {
        String sql = "UPDATE membre SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getEmail());
            stmt.setString(4, membre.getTelephone());
            stmt.setString(5, membre.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM membre WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();
        membre.setId(rs.getString("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setEmail(rs.getString("email"));
        membre.setTelephone(rs.getString("telephone"));
        return membre;
    }
}
