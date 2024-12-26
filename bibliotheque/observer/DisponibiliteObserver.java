package com.ipsas.bibliotheque.observer;

import com.ipsas.bibliotheque.model.Ouvrage;

public interface DisponibiliteObserver {
    void notifierDisponibilite(Ouvrage ouvrage);
}
