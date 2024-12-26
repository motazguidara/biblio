package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.MagazinePapier;

public class MagazinePapierFactory extends OuvrageFactory {
    @Override
    public Ouvrage createOuvrage() {
        return new MagazinePapier();
    }
}
