package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.LivreNumerique;

public class LivreNumeriqueFactory extends OuvrageFactory {
    @Override
    public Ouvrage createOuvrage() {
        return new LivreNumerique();
    }
}
