package com.ipsas.bibliotheque.command;

import com.ipsas.bibliotheque.model.*;
import java.time.LocalDateTime;

public class EmpruntCommand implements Command {
    private final Membre membre;
    private final Exemplaire exemplaire;

    public EmpruntCommand(Membre membre, Exemplaire exemplaire) {
        this.membre = membre;
        this.exemplaire = exemplaire;
    }

    @Override
    public void executer() {
        if (!exemplaire.isDisponible()) {
            throw new IllegalStateException("L'exemplaire n'est pas disponible");
        }

        Emprunt emprunt = new Emprunt();
        emprunt.setId(java.util.UUID.randomUUID().toString());
        emprunt.setMembreId(membre.getId());
        emprunt.setExemplaireId(exemplaire.getId());
        emprunt.setDateEmprunt(LocalDateTime.now());
        emprunt.setDateRetourPrevue(LocalDateTime.now().plusDays(14));
        emprunt.setStatut(Emprunt.StatutEmprunt.EN_COURS);

        exemplaire.setDisponible(false);
        if (membre.getEmprunts() == null) {
            membre.setEmprunts(new java.util.ArrayList<>());
        }
        membre.getEmprunts().add(emprunt);
    }
}
