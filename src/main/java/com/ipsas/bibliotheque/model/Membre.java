package com.ipsas.bibliotheque.model;

import lombok.Data;
import java.util.List;

@Data
public class Membre {
    private String id;
    private String nom;
    private String prenom;
    private String adresse;
    private String email;
    private String telephone;
    private List<Emprunt> emprunts;
}
