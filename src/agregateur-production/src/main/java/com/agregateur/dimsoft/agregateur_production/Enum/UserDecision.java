package com.agregateur.dimsoft.agregateur_production.Enum;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

public enum UserDecision implements ObservableBooleanValue {

    ANNULER_AGREGATION,
    CONTINUER_AVEC_REDONDANCE,
    CONTINUER_SANS_REDONDANCE;

    /**
     * @return
     */
    @Override
    public boolean get() {
        return false;
    }

    /**
     * @param listener The listener to register
     */
    @Override
    public void addListener(ChangeListener<? super Boolean> listener) {

    }

    /**
     * @param listener The listener to remove
     */
    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {

    }

    /**
     * @return
     */
    @Override
    public Boolean getValue() {
        return null;
    }

    /**
     * @param listener The listener to register
     */
    @Override
    public void addListener(InvalidationListener listener) {

    }

    /**
     * @param listener The listener to remove
     */
    @Override
    public void removeListener(InvalidationListener listener) {

    }
}
