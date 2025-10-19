package com.agregateur.dimsoft.agregateur_production.services.impl;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class ParseurCAFile {


    private static final int LIGNE_ENTETES_CA = 10;  // Ligne 11 (index 10)
    private static final char SEPARATEUR = ';';
    private String numeroCompte;

    /**
     * Parse un fichier CA
     */
    public List<Map<String, String>> parserFichier(String cheminFichier) {
        validationFichier(cheminFichier);
        List<Map<String, String>> donnees = new ArrayList<>();

        try (FileReader fileReader = new FileReader(cheminFichier);
             CSVReader csvReader = new CSVReaderBuilder(fileReader)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator(SEPARATEUR)
                             .build())
                     .withSkipLines(0)
                     .build()) {

            List<String[]> toutesLesLignes = csvReader.readAll();

            if (toutesLesLignes.size() <= LIGNE_ENTETES_CA) {
                throw new IllegalStateException("Le fichier CA est invalide ou trop court");
            }

            // Extraire le numéro de compte (si présent)
            extraireNumeroCompte(toutesLesLignes);

            // Récupérer les en-têtes à la ligne 11
            String[] entetes = toutesLesLignes.get(LIGNE_ENTETES_CA);
            entetes = nettoyerEntetes(entetes);

            // Traiter les lignes de données à partir de la ligne 12
            for (int i = LIGNE_ENTETES_CA + 1; i < toutesLesLignes.size(); i++) {
                String[] ligne = toutesLesLignes.get(i);

                if (estLigneVide(ligne)) {
                    continue;
                }

                Map<String, String> enregistrement = new HashMap<>();

                for (int j = 0; j < entetes.length && j < ligne.length; j++) {
                    String cle = entetes[j].trim();
                    String valeur = ligne[j] != null ? ligne[j].trim() : "";
                    enregistrement.put(cle, valeur);
                }

                donnees.add(enregistrement);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Erreur de lecture du fichier CA : " + e.getMessage(), e);
        } catch (CsvException e) {
            throw new IllegalStateException("Erreur de parsing du fichier CA : " + e.getMessage(), e);
        }

        return donnees;
    }

    /**
     * Extrait le numéro de compte du fichier CA
     */
    private void extraireNumeroCompte(List<String[]> lignes) {
        // Le numéro de compte peut être dans les premières lignes
        for (int i = 0; i < Math.min(10, lignes.size()); i++) {
            String[] ligne = lignes.get(i);
            if (ligne.length > 0) {
                String contenu = ligne[0];
                if (contenu.toLowerCase().contains("compte")) {
                    // Chercher un numéro dans cette ligne
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{5,}");
                    java.util.regex.Matcher matcher = pattern.matcher(contenu);
                    if (matcher.find()) {
                        this.numeroCompte = matcher.group();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Vérifie la structure du fichier CA
     */
    public boolean verifierStructure(String cheminFichier) {
        try (FileReader fileReader = new FileReader(cheminFichier);
             CSVReader csvReader = new CSVReaderBuilder(fileReader)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator(SEPARATEUR)
                             .build())
                     .build()) {

            List<String[]> lignes = csvReader.readAll();

            if (lignes.size() <= LIGNE_ENTETES_CA) {
                return false;
            }

            String[] entetes = lignes.get(LIGNE_ENTETES_CA);
            String[] enteteNettoyees = nettoyerEntetes(entetes);

            // Vérifier la présence des colonnes requises
            List<String> colonnesRequises = Arrays.asList("Date", "Libellé", "Débit euros", "Crédit euros");

            for (String colonne : colonnesRequises) {
                boolean trouve = false;
                for (String entete : enteteNettoyees) {
                    if (entete.toLowerCase().contains(colonne.toLowerCase())) {
                        trouve = true;
                        break;
                    }
                }
                if (!trouve) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private String[] nettoyerEntetes(String[] entetes) {
        String[] enteteNettoyees = new String[entetes.length];
        for (int i = 0; i < entetes.length; i++) {
            String entete = entetes[i];
            if (entete != null) {
                entete = entete.trim()
                        .replaceAll("[^\\w\\s']", "")
                        .replaceAll("\\s+", " ")
                        .trim();
            }
            enteteNettoyees[i] = entete;
        }
        return enteteNettoyees;
    }

    private boolean estLigneVide(String[] ligne) {
        if (ligne == null || ligne.length == 0) {
            return true;
        }
        for (String cellule : ligne) {
            if (cellule != null && !cellule.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void validationFichier(String cheminFichier) {
        Path path = Paths.get(cheminFichier);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Le fichier n'existe pas : " + cheminFichier);
        }

        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("Le fichier n'est pas accessible en lecture : " + cheminFichier);
        }
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }
}
