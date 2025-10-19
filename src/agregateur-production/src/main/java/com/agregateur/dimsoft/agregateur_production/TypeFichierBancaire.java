package com.agregateur.dimsoft.agregateur_production;

import lombok.Getter;

@Getter
public enum TypeFichierBancaire {
    CA("Crédit Agricole"),
    LCL("LCL"),
    INCONNU("Inconnu");

    private final String nom;

    TypeFichierBancaire(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
}