package com.ipsas.bibliotheque;

import com.ipsas.bibliotheque.db.DatabaseConnection;
import com.ipsas.bibliotheque.model.*;
import com.ipsas.bibliotheque.factory.OuvrageFactory;
import com.ipsas.bibliotheque.strategy.*;
import com.ipsas.bibliotheque.command.*;
import com.ipsas.bibliotheque.observer.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

public class BibliothequeDemo {
    private static final Logger LOGGER = Logger.getLogger(BibliothequeDemo.class.getName());

    static {
        // Configure logger to show all messages
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
    }

    public static void main(String[] args) {
        // 1. Démonstration du Singleton (Database Connection)
        DatabaseConnection db = DatabaseConnection.getInstance();
        LOGGER.info("Connexion à la base de données établie.");

        // Add shutdown hook to properly close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            db.shutdown();
            LOGGER.info("Connexion à la base de données fermée.");
        }));

        // 2. Démonstration de la Factory
        OuvrageFactory livrePapierFactory = OuvrageFactory.getFactory(Ouvrage.TypeOuvrage.LIVRE, false);
        Ouvrage livre = livrePapierFactory.createOuvrage();
        LOGGER.info("Livre créé avec succès: " + livre);

        // 3. Démonstration de la Strategy (Recherche)
        RechercheStrategy rechercheMonoCritere = new RechercheMono();
        RechercheStrategy rechercheMultiCritere = new RechercheMulti();

        // Recherche mono-critère
        Map<String, Object> criteresMono = new HashMap<>();
        criteresMono.put("motCle", "Le Petit");
        List<Ouvrage> resultatsMono = rechercheMonoCritere.rechercher(criteresMono);
        
        LOGGER.info("Résultats de la recherche mono-critère:");
        if (resultatsMono.isEmpty()) {
            LOGGER.warning("Aucun résultat trouvé pour la recherche mono-critère.");
        } else {
            for (Ouvrage o : resultatsMono) {
                LOGGER.info(o.toString());
            }
        }

        // Recherche multi-critères
        Map<String, Object> criteresMulti = new HashMap<>();
        criteresMulti.put("motCle", "Le Petit");
        criteresMulti.put("auteur", "Saint-Exupéry");
        List<Ouvrage> resultatsMulti = rechercheMultiCritere.rechercher(criteresMulti);
        
        LOGGER.info("Résultats de la recherche multi-critères:");
        if (resultatsMulti.isEmpty()) {
            LOGGER.warning("Aucun résultat trouvé pour la recherche multi-critères.");
        } else {
            for (Ouvrage o : resultatsMulti) {
                LOGGER.info(o.toString());
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
                LOGGER.info("Notification envoyée à " + membre.getNom() + 
                            " : L'ouvrage est maintenant disponible! " + ouvrage);
            }
        };

        disponibiliteSubject.attach(observerMembre);
        disponibiliteSubject.notifyObservers(livre);

        // 5. Démonstration du Command
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setNumero("EX001");
        exemplaire.setOuvrage(livre);
        
        BibliothequeCommand empruntCommand = new EmpruntCommand(membre, exemplaire);
        empruntCommand.execute();
        LOGGER.info("Emprunt effectué.");

        BibliothequeCommand retourCommand = new RetourCommand(membre.getEmprunts().get(0));
        retourCommand.execute();
        LOGGER.info("Retour effectué.");

        LOGGER.info("Démonstration terminée!");
    }
}
