-- Table pour les réservations
CREATE TABLE IF NOT EXISTS reservation (
    id VARCHAR(36) PRIMARY KEY,
    membre_id VARCHAR(36) NOT NULL,
    ouvrage_id VARCHAR(36) NOT NULL,
    date_reservation TIMESTAMP NOT NULL,
    date_notification TIMESTAMP,
    date_annulation TIMESTAMP,
    statut VARCHAR(20) NOT NULL, -- EN_ATTENTE, NOTIFIEE, ANNULEE, TERMINEE
    FOREIGN KEY (membre_id) REFERENCES membre(id),
    FOREIGN KEY (ouvrage_id) REFERENCES ouvrage(id)
);

-- Table pour les notifications
CREATE TABLE IF NOT EXISTS notification (
    id VARCHAR(36) PRIMARY KEY,
    emprunt_id VARCHAR(36),
    type VARCHAR(20) NOT NULL, -- RETARD, DISPONIBILITE
    message TEXT NOT NULL,
    date_envoi TIMESTAMP NOT NULL,
    FOREIGN KEY (emprunt_id) REFERENCES emprunt(id)
);

-- Ajout des colonnes pour la gestion des retards dans la table emprunt
ALTER TABLE emprunt 
ADD COLUMN IF NOT EXISTS notification_envoyee BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS mesures_disciplinaires_appliquees BOOLEAN DEFAULT FALSE;

-- Ajout des colonnes pour les mots-clés dans la table ouvrage
ALTER TABLE ouvrage
ADD COLUMN IF NOT EXISTS mots_cles TEXT;
