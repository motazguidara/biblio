package com.ipsas.bibliotheque.observer;

import com.ipsas.bibliotheque.model.Ouvrage;
import java.util.ArrayList;
import java.util.List;

public class DisponibiliteSubject {
    private List<DisponibiliteObserver> observers = new ArrayList<>();
    
    public void ajouterObserver(DisponibiliteObserver observer) {
        observers.add(observer);
    }
    
    public void supprimerObserver(DisponibiliteObserver observer) {
        observers.remove(observer);
    }
    
    public void notifierDisponibilite(Ouvrage ouvrage) {
        for (DisponibiliteObserver observer : observers) {
            observer.notifierDisponibilite(ouvrage);
        }
    }
}
