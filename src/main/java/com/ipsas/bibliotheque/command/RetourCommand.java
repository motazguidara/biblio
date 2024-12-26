package com.ipsas.bibliotheque.command;

import com.ipsas.bibliotheque.model.*;
import java.time.LocalDateTime;

public class RetourCommand implements Command {
    private final Emprunt emprunt;

    public RetourCommand(Emprunt emprunt) {
        this.emprunt = emprunt;
    }

    @Override
    public void executer() {
        if (emprunt.getStatut() != Emprunt.StatutEmprunt.EN_COURS && 
            emprunt.getStatut() != Emprunt.StatutEmprunt.EN_RETARD) {
            throw new IllegalStateException("L'emprunt n'est pas en cours");
        }

        emprunt.setDateRetourEffective(LocalDateTime.now());
        emprunt.setStatut(Emprunt.StatutEmprunt.RETOURNE);

        // Mettre à jour la disponibilité de l'exemplaire
        // Note: Dans une vraie application, on récupérerait l'exemplaire depuis une base de données
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setId(emprunt.getExemplaireId());
        exemplaire.setDisponible(true);
    }
}
