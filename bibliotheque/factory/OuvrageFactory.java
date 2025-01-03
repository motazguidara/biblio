package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;

public abstract class OuvrageFactory {
    // Abstract method for creating an Ouvrage
    public abstract Ouvrage createOuvrage();

    // Factory method to get the appropriate factory based on the type and digital
    // status
    public static OuvrageFactory getFactory(Ouvrage.TypeOuvrage type, boolean estNumerique) {
        if (estNumerique) {
            switch (type) {
                case LIVRE:
                    return new LivreNumeriqueFactory();
                case REVUE:
                    return new RevueNumeriqueFactory();
                case MAGAZINE:
                    return new MagazineNumeriqueFactory();
                default:
                    throw new IllegalArgumentException("Type d'ouvrage non supporté pour numérique");
            }
        } else {
            switch (type) {
                case LIVRE:
                    return new LivrePapierFactory();
                case REVUE:
                    return new RevuePapierFactory();
                case MAGAZINE:
                    return new MagazinePapierFactory();
                default:
                    throw new IllegalArgumentException("Type d'ouvrage non supporté pour papier");
            }
        }
    }
}
