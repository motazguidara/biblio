package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.RevueNumerique;

public class RevueNumeriqueFactory extends OuvrageFactory {
    @Override
    public Ouvrage createOuvrage() {
        return new RevueNumerique();
    }
}
