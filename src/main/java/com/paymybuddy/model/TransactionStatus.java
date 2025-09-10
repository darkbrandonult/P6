package com.paymybuddy.model;

/**
 * Énumération représentant les différents statuts d'une transaction
 */
public enum TransactionStatus {
    PENDING("En attente"),
    COMPLETED("Terminée"),
    FAILED("Échouée");

    private final String displayName;

    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
