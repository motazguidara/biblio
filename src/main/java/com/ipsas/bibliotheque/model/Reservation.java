package com.ipsas.bibliotheque.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private String id;
    private String membreId;
    private String ouvrageId;
    private LocalDateTime dateReservation;
    private LocalDateTime dateNotification;
    private LocalDateTime dateAnnulation;
    private StatutReservation statut;

    public enum StatutReservation {
        EN_ATTENTE,
        NOTIFIEE,
        ANNULEE,
        TERMINEE
    }
}
