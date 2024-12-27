package com.ipsas.bibliotheque;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.*;
import com.ipsas.bibliotheque.factory.OuvrageFactory;
import com.ipsas.bibliotheque.strategy.*;
import com.ipsas.bibliotheque.command.*;
import com.ipsas.bibliotheque.observer.*;
import com.ipsas.bibliotheque.service.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class BibliothequeDemo {
    public static void main(String[] args) {
        try {
            // 1. Démonstration du Singleton (Database Connection)
            DatabaseConnection db = DatabaseConnection.getInstance();
            System.out.println("Connexion à la base de données établie.");

            // 2. Démonstration de la Factory
            OuvrageFactory livrePapierFactory = OuvrageFactory.getFactory(Ouvrage.Type.LIVRE, false);
            Ouvrage livre = livrePapierFactory.createOuvrage();
            System.out.println("Livre créé avec succès.");

            // 3. Démonstration de la Strategy (Recherche)
            RechercheStrategy rechercheMonoCritere = new RechercheMono();
            RechercheStrategy rechercheMultiCritere = new RechercheMulti();

            // Recherche mono-critère
            Map<String, Object> criteresMono = new HashMap<>();
            criteresMono.put("titre", "Les Misérables");
            System.out.println("Recherche mono-critère avec le titre 'Les Misérables':");
            List<Ouvrage> resultatsMono = rechercheMonoCritere.rechercher(criteresMono);
            
            if (resultatsMono.isEmpty()) {
                System.out.println("Aucun résultat trouvé pour la recherche mono-critère.");
            } else {
                System.out.println("Résultats de la recherche mono-critère:");
                for (Ouvrage o : resultatsMono) {
                    System.out.println(o);
                }
            }

            // Recherche multi-critères
            Map<String, Object> criteresMulti = new HashMap<>();
            criteresMulti.put("titre", "L'Étranger");
            criteresMulti.put("auteur", "Albert Camus");
            criteresMulti.put("editeur", "Gallimard");
            System.out.println("\nRecherche multi-critères avec le titre 'L'Étranger', l'auteur 'Albert Camus' et l'éditeur 'Gallimard':");
            List<Ouvrage> resultatsMulti = rechercheMultiCritere.rechercher(criteresMulti);
            
            if (resultatsMulti.isEmpty()) {
                System.out.println("Aucun résultat trouvé pour la recherche multi-critères.");
            } else {
                System.out.println("Résultats de la recherche multi-critères:");
                for (Ouvrage o : resultatsMulti) {
                    System.out.println(o);
                }
            }

            // 4. Démonstration du système d'emprunt et de réservation
            EmpruntService empruntService = new EmpruntService();
            ReservationService reservationService = new ReservationService();

            // Créer un emprunt
            String membreId = "1"; // ID d'un membre existant
            String exemplaireId = "1"; // ID d'un exemplaire existant
            
            try {
                Emprunt emprunt = empruntService.emprunterOuvrage(membreId, exemplaireId);
                System.out.println("Emprunt créé avec succès: " + emprunt.getId());
                
                // Simuler un retard
                System.out.println("Vérification des emprunts en retard...");
                List<Emprunt> empruntsEnRetard = empruntService.getEmpruntsEnRetard();
                for (Emprunt e : empruntsEnRetard) {
                    System.out.println("Emprunt en retard: " + e.getId());
                }
                
                // Retourner l'ouvrage
                empruntService.retournerOuvrage(emprunt.getId());
                System.out.println("Ouvrage retourné avec succès");
                
            } catch (Exception e) {
                System.out.println("Erreur lors de l'emprunt: " + e.getMessage());
            }

            // 5. Démonstration du système de réservation
            String ouvrageId = "1"; // ID d'un ouvrage existant
            
            try {
                // Réserver un ouvrage
                reservationService.reserverOuvrage(membreId, ouvrageId);
                System.out.println("Réservation créée avec succès");
                
                // Vérifier les réservations en attente
                List<Reservation> reservationsEnAttente = reservationService.getReservationsEnAttente(ouvrageId);
                System.out.println("Nombre de réservations en attente: " + reservationsEnAttente.size());
                
                // Notifier la prochaine réservation
                reservationService.notifierProchaineReservation(ouvrageId);
                System.out.println("Notification envoyée au prochain membre sur la liste d'attente");
                
            } catch (Exception e) {
                System.out.println("Erreur lors de la réservation: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de la démonstration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
