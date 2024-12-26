package com.ipsas.bibliotheque.observer;

import com.ipsas.bibliotheque.model.Ouvrage;
import java.util.ArrayList;
import java.util.List;

public class DisponibiliteSubject {
    private List<DisponibiliteObserver> observers = new ArrayList<>();
    
    public void attach(DisponibiliteObserver observer) {
        observers.add(observer);
    }
    
    public void detach(DisponibiliteObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(Ouvrage ouvrage) {
        for (DisponibiliteObserver observer : observers) {
            observer.notifierDisponibilite(ouvrage);
        }
    }
}
