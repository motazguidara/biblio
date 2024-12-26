package com.ipsas.bibliotheque.service;

import com.ipsas.bibliotheque.dao.*;
import com.ipsas.bibliotheque.model.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BibliothequeService {
    private final OuvrageDAO ouvrageDAO;
    private final MembreDAO membreDAO;
    private final EmpruntDAO empruntDAO;
    private final ExemplaireDAO exemplaireDAO;

    public BibliothequeService() {
        this.ouvrageDAO = new OuvrageDAO();
        this.membreDAO = new MembreDAO();
        this.empruntDAO = new EmpruntDAO();
        this.exemplaireDAO = new ExemplaireDAO();
    }

    // Ouvrage operations
    public Ouvrage ajouterOuvrage(Ouvrage ouvrage) throws SQLException {
        return ouvrageDAO.save(ouvrage);
    }

    public List<Ouvrage> listerOuvrages() throws SQLException {
        return ouvrageDAO.findAll();
    }

    public Optional<Ouvrage> trouverOuvrage(String id) throws SQLException {
        return ouvrageDAO.findById(id);
    }

    // Membre operations
    public Membre ajouterMembre(Membre membre) throws SQLException {
        return membreDAO.save(membre);
    }

    public List<Membre> listerMembres() throws SQLException {
        return membreDAO.findAll();
    }

    public Optional<Membre> trouverMembre(String id) throws SQLException {
        return membreDAO.findById(id);
    }

    // Emprunt operations
    public Emprunt emprunterOuvrage(String membreId, String exemplaireId) throws SQLException {
        // Verify membre exists
        Optional<Membre> membre = membreDAO.findById(membreId);
        if (!membre.isPresent()) {
            throw new IllegalArgumentException("Membre non trouvé");
        }

        // Verify exemplaire exists and is available
        Optional<Exemplaire> exemplaire = exemplaireDAO.findById(exemplaireId);
        if (!exemplaire.isPresent()) {
            throw new IllegalArgumentException("Exemplaire non trouvé");
        }
        if (!exemplaire.get().isDisponible()) {
            throw new IllegalStateException("Exemplaire non disponible");
        }

        // Create emprunt
        Emprunt emprunt = new Emprunt();
        emprunt.setMembreId(membreId);
        emprunt.setExemplaireId(exemplaireId);
        emprunt.setDateEmprunt(LocalDateTime.now());
        emprunt.setDateRetourPrevue(LocalDateTime.now().plusDays(14)); // 2 weeks loan period

        // Update exemplaire availability
        exemplaire.get().setDisponible(false);
        exemplaireDAO.update(exemplaire.get());

        return empruntDAO.save(emprunt);
    }

    public void retournerOuvrage(String empruntId) throws SQLException {
        Optional<Emprunt> emprunt = empruntDAO.findById(empruntId);
        if (!emprunt.isPresent()) {
            throw new IllegalArgumentException("Emprunt non trouvé");
        }

        // Update emprunt
        emprunt.get().setDateRetourEffective(LocalDateTime.now());
        empruntDAO.update(emprunt.get());

        // Update exemplaire availability
        Optional<Exemplaire> exemplaire = exemplaireDAO.findById(emprunt.get().getExemplaireId());
        if (exemplaire.isPresent()) {
            exemplaire.get().setDisponible(true);
            exemplaireDAO.update(exemplaire.get());
        }
    }

    public List<Emprunt> listerEmprunts() throws SQLException {
        return empruntDAO.findAll();
    }

    public List<Emprunt> listerEmpruntsMembre(String membreId) throws SQLException {
        return empruntDAO.findByMembre(membreId);
    }

    // Exemplaire operations
    public Exemplaire ajouterExemplaire(Exemplaire exemplaire) throws SQLException {
        return exemplaireDAO.save(exemplaire);
    }

    public List<Exemplaire> listerExemplaires() throws SQLException {
        return exemplaireDAO.findAll();
    }

    public List<Exemplaire> listerExemplairesOuvrage(String ouvrageId) throws SQLException {
        return exemplaireDAO.findByOuvrage(ouvrageId);
    }

    public Optional<Exemplaire> trouverExemplaire(String id) throws SQLException {
        return exemplaireDAO.findById(id);
    }
}
