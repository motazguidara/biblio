package com.ipsas.bibliotheque.factory;

import com.ipsas.bibliotheque.model.Ouvrage;

public abstract class OuvrageFactory {
    public abstract Ouvrage createOuvrage();
    
    // Méthode factory pour obtenir la factory appropriée
    public static OuvrageFactory getFactory(Ouvrage.Type type, boolean estNumerique) {
        if (estNumerique) {
            switch (type) {
                case LIVRE:
                    return new LivreNumeriqueFactory();
                case REVUE:
                    return new RevueNumeriqueFactory();
                case MAGAZINE:
                    return new MagazineNumeriqueFactory();
                default:
                    throw new IllegalArgumentException("Type d'ouvrage non supporté");
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
                    throw new IllegalArgumentException("Type d'ouvrage non supporté");
            }
        }
    }
}
