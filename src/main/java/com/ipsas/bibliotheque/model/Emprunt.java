package com.ipsas.bibliotheque.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Emprunt {
    private String id;
    private String membreId;
    private String exemplaireId;
    private LocalDateTime dateEmprunt;
    private LocalDateTime dateRetourPrevue;
    private LocalDateTime dateRetourEffective;
    private StatutEmprunt statut;
    
    public enum StatutEmprunt {
        EN_COURS,
        EN_RETARD,
        RETOURNE
    }
}
