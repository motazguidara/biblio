package com.ipsas.bibliotheque.command;

import com.ipsas.bibliotheque.model.Membre;
import com.ipsas.bibliotheque.model.Exemplaire;
import com.ipsas.bibliotheque.model.Emprunt;
import java.time.LocalDateTime;

public class EmpruntCommand implements BibliothequeCommand {
    private final Membre membre;
    private final Exemplaire exemplaire;
    private Emprunt emprunt;

    public EmpruntCommand(Membre membre, Exemplaire exemplaire) {
        this.membre = membre;
        this.exemplaire = exemplaire;
    }

    @Override
    public void execute() {
        if (!exemplaire.isEstEmprunte()) {
            emprunt = new Emprunt();
            emprunt.setMembre(membre);
            emprunt.setExemplaire(exemplaire);
            emprunt.setDateEmprunt(LocalDateTime.now());
            emprunt.setDateRetourPrevue(LocalDateTime.now().plusDays(3));
            emprunt.setStatut(Emprunt.StatutEmprunt.EN_COURS);
            
            exemplaire.setEstEmprunte(true);
            membre.getEmprunts().add(emprunt);
        }
    }

    @Override
    public void undo() {
        if (emprunt != null) {
            membre.getEmprunts().remove(emprunt);
            exemplaire.setEstEmprunte(false);
            emprunt.setStatut(Emprunt.StatutEmprunt.RETOURNE);
        }
    }
}
