package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.LivrePapier;

public class LivrePapierFactory extends OuvrageFactory {
    @Override
    public Ouvrage createOuvrage() {
        return new LivrePapier();
    }
}
