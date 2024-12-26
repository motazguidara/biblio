package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;
import com.ipsas.bibliotheque.model.MagazineNumerique;

public class MagazineNumeriqueFactory extends OuvrageFactory {
    @Override
    public Ouvrage createOuvrage() {
        return new MagazineNumerique();
    }
}
