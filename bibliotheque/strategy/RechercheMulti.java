package com.ipsas.bibliotheque.strategy;

import com.ipsas.bibliotheque.model.Ouvrage;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class RechercheMulti implements RechercheStrategy {
    @Override
    public List<Ouvrage> rechercher(Map<String, Object> criteres) {
        // Logique de recherche avec plusieurs critères
        return new ArrayList<>(); // À implémenter avec la vraie logique
    }
}
