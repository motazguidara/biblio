-- Create Database
CREATE DATABASE IF NOT EXISTS bibliotheque;
USE bibliotheque;

-- Create Ouvrage (Book) Table
CREATE TABLE IF NOT EXISTS ouvrage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    annee_publication INT,
    disponible BOOLEAN DEFAULT TRUE
);

-- Create Adherent (Member) Table
CREATE TABLE IF NOT EXISTS adherent (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    date_inscription DATE
);

-- Create Emprunt (Loan) Table
CREATE TABLE IF NOT EXISTS emprunt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ouvrage_id INT,
    adherent_id INT,
    date_emprunt DATE,
    date_retour_prevue DATE,
    date_retour_effectif DATE,
    FOREIGN KEY (ouvrage_id) REFERENCES ouvrage(id),
    FOREIGN KEY (adherent_id) REFERENCES adherent(id)
);

-- Insert Test Data for Ouvrages
INSERT INTO ouvrage (titre, auteur, isbn, annee_publication, disponible) VALUES
('Le Petit Prince', 'Antoine de Saint-Exupéry', '9782070408504', 1943, TRUE),
('1984', 'George Orwell', '9782070368228', 1949, TRUE),
('L\'Étranger', 'Albert Camus', '9782070360024', 1942, TRUE),
('Harry Potter à l\'école des sorciers', 'J.K. Rowling', '9782070541279', 1997, TRUE),
('Le Code Da Vinci', 'Dan Brown', '9782221106587', 2003, TRUE);

-- Insert Test Data for Adherents
INSERT INTO adherent (nom, prenom, email, date_inscription) VALUES
('Dupont', 'Jean', 'jean.dupont@example.com', '2024-01-15'),
('Martin', 'Sophie', 'sophie.martin@example.com', '2024-02-01'),
('Lefebvre', 'Pierre', 'pierre.lefebvre@example.com', '2024-03-10');

-- Insert Test Loan Data
INSERT INTO emprunt (ouvrage_id, adherent_id, date_emprunt, date_retour_prevue) VALUES
(1, 1, '2024-04-01', '2024-04-15'),
(2, 2, '2024-04-05', '2024-04-20');
