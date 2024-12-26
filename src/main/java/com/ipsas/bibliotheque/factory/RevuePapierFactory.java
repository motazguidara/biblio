package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.RevuePapier;

public class RevuePapierFactory extends OuvrageFactory {
    @Override
    public Ouvrage createOuvrage() {
        return new RevuePapier();
    }
}
