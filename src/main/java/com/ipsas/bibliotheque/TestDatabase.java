package com.ipsas.bibliotheque;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {
    public static void main(String[] args) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        
        if (db.testConnection()) {
            System.out.println("✅ Connection successful!");
            
            // Test a simple query
            try (Connection conn = db.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                
                if (rs.next()) {
                    System.out.println("📚 Database query successful!");
                }
            } catch (Exception e) {
                System.err.println("❌ Error executing query: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ Could not connect to database!");
        }
    }
}
