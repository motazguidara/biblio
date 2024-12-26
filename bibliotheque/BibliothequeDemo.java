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

public class BibliothequeDemo {
    public static void main(String[] args) {
        // 1. Démonstration du Singleton (Database Connection)
        DatabaseConnection db = DatabaseConnection.getInstance();
        System.out.println("Connexion à la base de données établie.");

        // 2. Démonstration de la Factory
        OuvrageFactory livrePapierFactory = OuvrageFactory.getFactory(Ouvrage.TypeOuvrage.LIVRE, false);
        Ouvrage livre = livrePapierFactory.createOuvrage();
        System.out.println("Livre créé avec succès.");

        // 3. Démonstration de la Strategy (Recherche)
        RechercheStrategy rechercheMonoCritere = new RechercheMono();
        RechercheStrategy rechercheMultiCritere = new RechercheMulti();

        Map<String, Object> criteres = new HashMap<>();
        criteres.put("motCle", "Java");
        
        System.out.println("Recherche mono-critère:");
        rechercheMonoCritere.rechercher(criteres);
        
        criteres.put("auteur", "Martin Fowler");
        System.out.println("Recherche multi-critères:");
        rechercheMultiCritere.rechercher(criteres);

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

        disponibiliteSubject.attach(observerMembre);
        disponibiliteSubject.notifyObservers(livre);

        // 5. Démonstration du Command
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setNumero("EX001");
        exemplaire.setOuvrage(livre);
        
        BibliothequeCommand empruntCommand = new EmpruntCommand(membre, exemplaire);
        empruntCommand.execute();
        System.out.println("Emprunt effectué.");

        BibliothequeCommand retourCommand = new RetourCommand(membre.getEmprunts().get(0));
        retourCommand.execute();
        System.out.println("Retour effectué.");

        System.out.println("Démonstration terminée!");
    }
}
