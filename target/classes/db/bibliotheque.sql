-- Drop database if exists and create new one
DROP DATABASE IF EXISTS bibliotheque;
CREATE DATABASE bibliotheque;
USE bibliotheque;

-- Create tables for main entities
CREATE TABLE membre (
    id VARCHAR(36) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    adresse TEXT,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE editeur (
    id VARCHAR(36) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse TEXT
);

CREATE TABLE auteur (
    id VARCHAR(36) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    biographie TEXT
);

CREATE TABLE ouvrage (
    id VARCHAR(36) PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    isbn VARCHAR(13),
    annee INT,
    editeur_id VARCHAR(36),
    type VARCHAR(20) NOT NULL, -- LIVRE, REVUE, MAGAZINE
    format VARCHAR(20) NOT NULL, -- PAPIER, NUMERIQUE
    mots_cles TEXT,
    FOREIGN KEY (editeur_id) REFERENCES editeur(id)
);

CREATE TABLE exemplaire (
    id VARCHAR(36) PRIMARY KEY,
    ouvrage_id VARCHAR(36) NOT NULL,
    numero_exemplaire VARCHAR(50) NOT NULL,
    position_rayonnage VARCHAR(50),
    date_achat DATE,
    disponible BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ouvrage_id) REFERENCES ouvrage(id)
);

CREATE TABLE emprunt (
    id VARCHAR(36) PRIMARY KEY,
    membre_id VARCHAR(36) NOT NULL,
    exemplaire_id VARCHAR(36) NOT NULL,
    date_emprunt TIMESTAMP NOT NULL,
    date_retour_prevue TIMESTAMP NOT NULL,
    date_retour_effective TIMESTAMP,
    statut VARCHAR(20) NOT NULL, -- EN_COURS, RETOURNE, EN_RETARD
    notification_envoyee BOOLEAN DEFAULT FALSE,
    mesures_disciplinaires_appliquees BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (membre_id) REFERENCES membre(id),
    FOREIGN KEY (exemplaire_id) REFERENCES exemplaire(id)
);

-- Create tables for many-to-many relationships
CREATE TABLE ouvrage_auteur (
    ouvrage_id VARCHAR(36) NOT NULL,
    auteur_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (ouvrage_id, auteur_id),
    FOREIGN KEY (ouvrage_id) REFERENCES ouvrage(id),
    FOREIGN KEY (auteur_id) REFERENCES auteur(id)
);

-- Create tables for reservations and notifications
CREATE TABLE reservation (
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

CREATE TABLE notification (
    id VARCHAR(36) PRIMARY KEY,
    emprunt_id VARCHAR(36),
    type VARCHAR(20) NOT NULL, -- RETARD, DISPONIBILITE
    message TEXT NOT NULL,
    date_envoi TIMESTAMP NOT NULL,
    FOREIGN KEY (emprunt_id) REFERENCES emprunt(id)
);

-- Insert initial test data
INSERT INTO membre (id, nom, email, adresse) VALUES
('1', 'Dupont Jean', 'jean.dupont@email.com', '123 Rue de Paris'),
('2', 'Martin Sophie', 'sophie.martin@email.com', '456 Avenue des Fleurs');

INSERT INTO editeur (id, nom, adresse) VALUES
('1', 'Gallimard', 'Paris, France'),
('2', 'Flammarion', 'Paris, France');

INSERT INTO auteur (id, nom, prenom, biographie) VALUES
('1', 'Hugo', 'Victor', 'Écrivain français du XIXe siècle'),
('2', 'Camus', 'Albert', 'Écrivain et philosophe français'),
('3', 'de Saint-Exupéry', 'Antoine', 'Écrivain et aviateur français');

INSERT INTO ouvrage (id, titre, isbn, annee, editeur_id, type, format) VALUES
('1', 'Les Misérables', '9780140444308', 1862, '1', 'LIVRE', 'PAPIER'),
('2', 'L''Étranger', '9780679720201', 1942, '1', 'LIVRE', 'PAPIER'),
('3', 'Le Petit Prince', '9780156012195', 1943, '2', 'LIVRE', 'PAPIER');

INSERT INTO ouvrage_auteur (ouvrage_id, auteur_id) VALUES
('1', '1'),
('2', '2'),
('3', '3');

INSERT INTO exemplaire (id, ouvrage_id, numero_exemplaire, position_rayonnage, date_achat) VALUES
('1', '1', 'EX001', 'RAY-A1', '2023-01-01'),
('2', '1', 'EX002', 'RAY-A1', '2023-01-01'),
('3', '2', 'EX003', 'RAY-B2', '2023-01-01'),
('4', '3', 'EX004', 'RAY-C3', '2023-01-01');
