package com.ipsas.bibliotheque.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Exemplaire {
    private String id;
    private String ouvrageId;
    private String position;
    private LocalDate dateAchat;
    private boolean disponible;
    private Etat etat;

    public enum Etat {
        NEUF,
        BON,
        USAGE,
        MAUVAIS
    }
}
