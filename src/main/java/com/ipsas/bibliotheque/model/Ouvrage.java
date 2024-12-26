package com.ipsas.bibliotheque.model;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public abstract class Ouvrage {
    private String id;
    private String titre;
    private List<String> auteurs;
    private String editeur;
    private Set<String> motsClefs;
    private boolean estNumerique;
    private Type type;
    
    public enum Type {
        LIVRE,
        REVUE,
        MAGAZINE
    }
}
