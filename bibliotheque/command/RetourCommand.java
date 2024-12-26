package com.ipsas.bibliotheque.command;

import com.ipsas.bibliotheque.model.Emprunt;
import java.time.LocalDateTime;

public class RetourCommand implements BibliothequeCommand {
    private final Emprunt emprunt;
    private LocalDateTime ancienneDate;

    public RetourCommand(Emprunt emprunt) {
        this.emprunt = emprunt;
    }

    @Override
    public void execute() {
        if (emprunt.getStatut() == Emprunt.StatutEmprunt.EN_COURS) {
            ancienneDate = emprunt.getDateRetourEffective();
            emprunt.setDateRetourEffective(LocalDateTime.now());
            emprunt.setStatut(Emprunt.StatutEmprunt.RETOURNE);
            emprunt.getExemplaire().setEstEmprunte(false);
        }
    }

    @Override
    public void undo() {
        if (emprunt.getStatut() == Emprunt.StatutEmprunt.RETOURNE) {
            emprunt.setDateRetourEffective(ancienneDate);
            emprunt.setStatut(Emprunt.StatutEmprunt.EN_COURS);
            emprunt.getExemplaire().setEstEmprunte(true);
        }
    }
}
