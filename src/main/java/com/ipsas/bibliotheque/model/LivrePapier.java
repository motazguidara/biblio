package com.ipsas.bibliotheque.model;

import java.util.Collections;

public class LivrePapier extends Ouvrage {
    private String isbn;
    private int nombrePages;
    private int annee;
    private boolean disponible;

    // Constructor
    public LivrePapier() {
        setType(Type.LIVRE);
        setEstNumerique(false);
    }

    // Setters with proper implementation
    @Override
    public void setId(String id) {
        super.setId(id);
    }

    public void setId(int id) {
        super.setId(String.valueOf(id));
    }

    public void setAuteur(String auteur) {
        setAuteurs(Collections.singletonList(auteur));
    }

    public String getAuteur() {
        return getAuteurs() != null && !getAuteurs().isEmpty() ? getAuteurs().get(0) : null;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getAnnee() {
        return annee;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isDisponible() {
        return disponible;
    }

    // Getter and setter for nombrePages
    public int getNombrePages() {
        return nombrePages;
    }

    public void setNombrePages(int nombrePages) {
        this.nombrePages = nombrePages;
    }

    @Override
    public String toString() {
        return "LivrePapier{" +
                "id='" + getId() + '\'' +
                ", titre='" + getTitre() + '\'' +
                ", auteur='" + getAuteur() + '\'' +
                ", isbn='" + isbn + '\'' +
                ", annee=" + annee +
                ", disponible=" + disponible +
                ", nombrePages=" + nombrePages +
                '}';
    }
}
