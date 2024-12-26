package com.ipsas.bibliotheque;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.*;
import com.ipsas.bibliotheque.factory.OuvrageFactory;
import com.ipsas.bibliotheque.strategy.*;
import com.ipsas.bibliotheque.command.*;
import com.ipsas.bibliotheque.observer.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class BibliothequeDemo {
    public static void main(String[] args) {
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
        criteresMono.put("titre", "Le Petit Prince");
        criteresMono.put("anneePublication", 1943);
        System.out.println("Recherche mono-critère avec le titre 'Le Petit Prince' et l'année de publication 1943:");
        List<Ouvrage> resultatsMono = rechercheMonoCritere.rechercher(criteresMono);
        
        // Afficher les résultats de la recherche mono-critère
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
        criteresMulti.put("titre", "Le Petit Prince");
        criteresMulti.put("auteur", "Antoine de Saint-Exupéry");
        criteresMulti.put("editeur", "Reynal & Hitchcock");
        System.out.println("\nRecherche multi-critères avec le titre 'Le Petit Prince', l'auteur 'Antoine de Saint-Exupéry' et l'éditeur 'Reynal & Hitchcock':");
        List<Ouvrage> resultatsMulti = rechercheMultiCritere.rechercher(criteresMulti);
        
        // Afficher les résultats de la recherche multi-critères
        if (resultatsMulti.isEmpty()) {
            System.out.println("Aucun résultat trouvé pour la recherche multi-critères.");
        } else {
            System.out.println("Résultats de la recherche multi-critères:");
            for (Ouvrage o : resultatsMulti) {
                System.out.println(o);
            }
        }

        // 4. Démonstration de l'Observer
        DisponibiliteSubject disponibiliteSubject = new DisponibiliteSubject();
        Membre membre = new Membre();
        membre.setNom("John Doe");
        membre.setEmail("john.doe@example.com");
        membre.setEmprunts(new ArrayList<>());

        DisponibiliteObserver observerMembre = new DisponibiliteObserver() {
            @Override
            public void notifierDisponibilite(Ouvrage ouvrage) {
                System.out.println("Notification envoyée à " + membre.getNom() +
                        " : L'ouvrage est maintenant disponible!");
            }
        };

        disponibiliteSubject.ajouterObserver(observerMembre);
        disponibiliteSubject.notifierDisponibilite(livre);

        // 5. Démonstration du Command Pattern
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId("EX001");
        exemplaire.setOuvrageId(livre.getId());
        exemplaire.setDisponible(true);

        // Création et exécution de la commande d'emprunt
        EmpruntCommand empruntCommand = new EmpruntCommand(membre, exemplaire);
        empruntCommand.executer();

        // Création et exécution de la commande de retour
        RetourCommand retourCommand = new RetourCommand(membre.getEmprunts().get(0));
        retourCommand.executer();
    }
}
