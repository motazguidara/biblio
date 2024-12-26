package com.ipsas.bibliotheque.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ouvrage {
    private int id;
    private String titre;
    private String auteur;
    private String isbn;
    private int anneePublication;
    private boolean disponible;

    // Enum for book types
    public enum TypeOuvrage {
        LIVRE,
        MAGAZINE,
        REVUE
    }

    @Override
    public String toString() {
        return String.format("Livre: %s par %s (ISBN: %s, Année: %d, Disponible: %s)", 
                             titre, auteur, isbn, anneePublication, disponible ? "Oui" : "Non");
    }
}
