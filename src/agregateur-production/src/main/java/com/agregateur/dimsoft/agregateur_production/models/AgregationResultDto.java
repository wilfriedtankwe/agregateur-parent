package com.agregateur.dimsoft.agregateur_production.models;

import java.util.ArrayList;
import java.util.List;

public class AgregationResultDto {// Nombre total de transactions traitées
    private int totalTransactions;

    // Nombre de transactions insérées en base
    private int savedTransactions;

    // Nombre de doublons détectés / ignorés
    private int duplicateTransactions;

    // Nombre de transactions rejetées (erreurs ou refus)
    private int rejectedTransactions;

    // Détails des identifiants des transactions sauvegardées
    private List<Long> savedIds = new ArrayList<>();

    // Détails des identifiants des transactions en doublon
    private List<Long> duplicateIds = new ArrayList<>();

    // Détails des identifiants des transactions rejetées
    private List<Long> rejectedIds = new ArrayList<>();

    // Messages de log, d’erreur ou d’information
    private List<String> messages = new ArrayList<>();

    // Statut global de succès
    private boolean success;

    // Constructeur public par défaut
    public AgregationResultDto() {
    }

    // --- Getters / Setters ---
    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getSavedTransactions() {
        return savedTransactions;
    }

    public void setSavedTransactions(int savedTransactions) {
        this.savedTransactions = savedTransactions;
    }

    public int getDuplicateTransactions() {
        return duplicateTransactions;
    }

    public void setDuplicateTransactions(int duplicateTransactions) {
        this.duplicateTransactions = duplicateTransactions;
    }

    public int getRejectedTransactions() {
        return rejectedTransactions;
    }

    public void setRejectedTransactions(int rejectedTransactions) {
        this.rejectedTransactions = rejectedTransactions;
    }

    public List<Long> getSavedIds() {
        return savedIds;
    }

    public void setSavedIds(List<Long> savedIds) {
        this.savedIds = savedIds;
    }

    public List<Long> getDuplicateIds() {
        return duplicateIds;
    }

    public void setDuplicateIds(List<Long> duplicateIds) {
        this.duplicateIds = duplicateIds;
    }

    public List<Long> getRejectedIds() {
        return rejectedIds;
    }

    public void setRejectedIds(List<Long> rejectedIds) {
        this.rejectedIds = rejectedIds;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // --- Méthodes utilitaires simples ---
    public void addMessage(String message) {
        this.messages.add(message);
    }

    public void addSavedId(Long id) {
        this.savedIds.add(id);
        this.savedTransactions++;
    }

    public void addDuplicateId(Long id) {
        this.duplicateIds.add(id);
        this.duplicateTransactions++;
    }

    public void addRejectedId(Long id) {
        this.rejectedIds.add(id);
        this.rejectedTransactions++;
    }

    // --- Méthodes statiques de construction attendues par le service ---

    /**
     * Annulation complète : aucune transaction n'a été enregistrée.
     */
    public static AgregationResultDto cancelled(String message) {
        AgregationResultDto result = new AgregationResultDto();
        result.setSuccess(false);
        result.addMessage(message);
        result.setTotalTransactions(0);
        result.setSavedTransactions(0);
        result.setDuplicateTransactions(0);
        result.setRejectedTransactions(0);
        return result;
    }

    /**
     * Succès : toutes les transactions fournies ont été sauvegardées (pas de doublons ignorés).
     * @param message message à renvoyer
     * @param savedCount nombre de transactions sauvegardées
     */
    public static AgregationResultDto success(String message, int savedCount) {
        AgregationResultDto result = new AgregationResultDto();
        result.setSuccess(true);
        result.addMessage(message);
        result.setSavedTransactions(savedCount);
        result.setDuplicateTransactions(0);
        result.setRejectedTransactions(0);
        result.setTotalTransactions(savedCount);
        return result;
    }

    /**
     * Succès partiel : certaines transactions ont été sauvegardées, d'autres ignorées (doublons).
     * @param message message à renvoyer
     * @param savedCount nombre de transactions sauvegardées
     * @param skippedCount nombre de transactions ignorées (doublons)
     */
    public static AgregationResultDto successWithSkipped(String message, int savedCount, int skippedCount) {
        AgregationResultDto result = new AgregationResultDto();
        result.setSuccess(true);
        result.addMessage(message);
        result.setSavedTransactions(savedCount);
        result.setDuplicateTransactions(skippedCount);
        result.setRejectedTransactions(0);
        result.setTotalTransactions(savedCount + skippedCount);
        return result;
    }

    @Override
    public String toString() {
        return "AgregationResultDto{" +
                "total=" + totalTransactions +
                ", saved=" + savedTransactions +
                ", duplicates=" + duplicateTransactions +
                ", rejected=" + rejectedTransactions +
                ", success=" + success +
                ", messages=" + messages +
                '}';
    }
}
