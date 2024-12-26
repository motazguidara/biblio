package com.ipsas.bibliotheque.strategy;

import com.ipsas.bibliotheque.model.Ouvrage;
import java.util.List;
import java.util.Map;

public interface RechercheStrategy {
    List<Ouvrage> rechercher(Map<String, Object> criteres);
}
