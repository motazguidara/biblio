-- Library Management System Database Initialization Script

-- Create Database
CREATE DATABASE IF NOT EXISTS bibliotheque;
USE bibliotheque;

-- Drop existing tables if they exist (to ensure clean setup)
DROP TABLE IF EXISTS livre;
DROP TABLE IF EXISTS auteur;
DROP TABLE IF EXISTS editeur;

-- Create Auteur (Author) Table
CREATE TABLE auteur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    nationalite VARCHAR(100)
);

-- Create Editeur (Publisher) Table
CREATE TABLE editeur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    pays VARCHAR(100)
);

-- Create Livre (Book) Table
CREATE TABLE livre (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    titre VARCHAR(255) NOT NULL,
    annee INT,
    nombre_pages INT,
    disponible BOOLEAN DEFAULT TRUE,
    auteur_id INT,
    editeur_id INT,
    type_livre VARCHAR(50), -- to distinguish between different types of books
    
    FOREIGN KEY (auteur_id) REFERENCES auteur(id),
    FOREIGN KEY (editeur_id) REFERENCES editeur(id)
);

-- Insert Sample Authors
INSERT INTO auteur (nom, prenom, nationalite) VALUES 
('Hugo', 'Victor', 'Français'),
('Tolkien', 'J.R.R', 'Britannique'),
('Rowling', 'J.K', 'Britannique'),
('Camus', 'Albert', 'Français');

-- Insert Sample Publishers
INSERT INTO editeur (nom, adresse, pays) VALUES 
('Gallimard', '5 Rue Sébastien Bottin, Paris', 'France'),
('Penguin Books', 'Strand, London', 'Royaume-Uni'),
('Seuil', '25 Boulevard Romain Rolland, Paris', 'France');

-- Insert Sample Books
INSERT INTO livre (isbn, titre, annee, nombre_pages, disponible, auteur_id, editeur_id, type_livre) VALUES 
('978-2070413119', 'Les Misérables', 1862, 1488, TRUE, 1, 1, 'Roman'),
('978-0261102385', 'The Lord of the Rings', 1954, 1178, TRUE, 2, 2, 'Fantasy'),
('978-0747532743', 'Harry Potter and the Philosopher\'s Stone', 1997, 223, TRUE, 3, 2, 'Fantasy'),
('978-2070360420', 'L\'Étranger', 1942, 123, TRUE, 4, 3, 'Roman');

-- Create a user for the application with proper permissions
CREATE USER IF NOT EXISTS 'bibliotheque_user'@'localhost' IDENTIFIED BY 'bibliotheque_pass';
GRANT ALL PRIVILEGES ON bibliotheque.* TO 'bibliotheque_user'@'localhost';
FLUSH PRIVILEGES;

-- Optional: Add some indexes to improve query performance
CREATE INDEX idx_livre_titre ON livre(titre);
CREATE INDEX idx_livre_annee ON livre(annee);
CREATE INDEX idx_auteur_nom ON auteur(nom);
CREATE INDEX idx_editeur_nom ON editeur(nom);

-- Print initialization complete message
SELECT 'Bibliothèque database initialized successfully!' AS message;
