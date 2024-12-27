package com.ipsas.bibliotheque.service;

import com.ipsas.bibliotheque.model.*;
import com.ipsas.bibliotheque.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.mail.*;
import javax.mail.internet.*;

public class NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "bibliotheque.ipsas@gmail.com";

    public void planifierRappelRetour(Emprunt emprunt) {
        // Dans une application réelle, on utiliserait un scheduler comme Quartz
        // Pour cet exemple, on simule juste la planification
        LOGGER.info("Rappel planifié pour l'emprunt: " + emprunt.getId());
    }

    public void envoyerRappelRetard(Emprunt emprunt) {
        try {
            // Récupérer les informations du membre
            Membre membre = getMembre(emprunt.getMembreId());
            // Récupérer les informations de l'ouvrage
            Ouvrage ouvrage = getOuvrage(emprunt.getExemplaireId());

            String sujet = "Rappel : Retour d'ouvrage en retard";
            String message = String.format(
                "Cher(e) %s,\n\n" +
                "Nous vous rappelons que l'ouvrage \"%s\" que vous avez emprunté " +
                "devait être retourné le %s.\n" +
                "Merci de le retourner dès que possible pour éviter des sanctions.\n\n" +
                "Cordialement,\n" +
                "La Bibliothèque IPSAS",
                membre.getNom(),
                ouvrage.getTitre(),
                emprunt.getDateRetourPrevue()
            );

            envoyerEmail(membre.getEmail(), sujet, message);
            
            // Enregistrer la notification dans la base de données
            enregistrerNotification(emprunt.getId(), "RETARD", message);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi du rappel de retard", e);
        }
    }

    public void notifierDisponibilite(String membreId, String ouvrageId) {
        try {
            Membre membre = getMembre(membreId);
            Ouvrage ouvrage = getOuvrage(ouvrageId);

            String sujet = "Ouvrage disponible";
            String message = String.format(
                "Cher(e) %s,\n\n" +
                "L'ouvrage \"%s\" que vous aviez réservé est maintenant disponible.\n" +
                "Vous pouvez venir le récupérer à la bibliothèque.\n\n" +
                "Cordialement,\n" +
                "La Bibliothèque IPSAS",
                membre.getNom(),
                ouvrage.getTitre()
            );

            envoyerEmail(membre.getEmail(), sujet, message);
            
            // Enregistrer la notification dans la base de données
            enregistrerNotification(null, "DISPONIBILITE", message);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la notification de disponibilité", e);
        }
    }

    private void envoyerEmail(String destinataire, String sujet, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Note: Dans une vraie application, ces credentials devraient être dans un fichier de configuration sécurisé
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, "votre_mot_de_passe_app");
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(EMAIL_FROM));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            mimeMessage.setSubject(sujet);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            LOGGER.info("Email envoyé avec succès à " + destinataire);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi de l'email", e);
        }
    }

    private void enregistrerNotification(String empruntId, String type, String message) throws SQLException {
        String sql = "INSERT INTO notification (id, emprunt_id, type, message, date_envoi) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, java.util.UUID.randomUUID().toString());
            pstmt.setString(2, empruntId);
            pstmt.setString(3, type);
            pstmt.setString(4, message);
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            pstmt.executeUpdate();
        }
    }

    private Membre getMembre(String membreId) throws SQLException {
        String sql = "SELECT * FROM membre WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, membreId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Membre membre = new Membre();
                    membre.setId(rs.getString("id"));
                    membre.setNom(rs.getString("nom"));
                    membre.setEmail(rs.getString("email"));
                    return membre;
                }
                throw new IllegalStateException("Membre non trouvé: " + membreId);
            }
        }
    }

    private Ouvrage getOuvrage(String exemplaireId) throws SQLException {
        String sql = "SELECT o.* FROM ouvrage o " +
                    "JOIN exemplaire e ON e.ouvrage_id = o.id " +
                    "WHERE e.id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exemplaireId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Pour simplifier, on crée un LivrePapier
                    LivrePapier ouvrage = new LivrePapier();
                    ouvrage.setId(rs.getString("id"));
                    ouvrage.setTitre(rs.getString("titre"));
                    return ouvrage;
                }
                throw new IllegalStateException("Ouvrage non trouvé pour l'exemplaire: " + exemplaireId);
            }
        }
    }
}
