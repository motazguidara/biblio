package com.ipsas.bibliotheque.strategy;

import com.ipsas.bibliotheque.model.Ouvrage;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class RechercheMono implements RechercheStrategy {
    @Override
    public List<Ouvrage> rechercher(Map<String, Object> criteres) {
        String motCle = (String) criteres.get("motCle");
        // Logique de recherche avec un seul critère
        return new ArrayList<>(); // À implémenter avec la vraie logique
    }
}
