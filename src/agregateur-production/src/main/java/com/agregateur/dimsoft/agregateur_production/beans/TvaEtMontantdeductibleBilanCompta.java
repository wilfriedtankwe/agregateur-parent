package com.agregateur.dimsoft.agregateur_production.beans;


import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

/**
 * @author Severin dimo
 * @author Baudouin Meli
 */

// alimenter avec un batch [3]
@Entity
@Table(name = "tvamontantdeductible")
@NamedQueries({
        @NamedQuery(name = "TvaEtMontantdeductibleBilanCompta.findAll", query = "SELECT t FROM TvaEtMontantdeductibleBilanCompta t"),
        @NamedQuery(name = "TvaEtMontantdeductibleBilanCompta.findById", query = "SELECT t FROM TvaEtMontantdeductibleBilanCompta t WHERE t.id = :id") })
public class TvaEtMontantdeductibleBilanCompta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "montantHtAvecTauxDeduction")
    private Float montantHtAvecTauxDeduction; // % CalculTVA.tauxDeduction.taux * CalculTVA.budget.montant
    @Column(name = "TVAAvecTauxDeduction")
    private Float TVAAvecTauxDeduction; // % CalculTVA.tauxDeduction.taux * CalculTVA.montantTVASansTaux

    // indicates that remove operations should be cascaded automatically to entity
    // objects that are referenced by that field (multiple entity objects can be
    // referenced by a collection field):
    @OneToOne(cascade = CascadeType.REMOVE)
    private CalculTVA calculTVA;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "last_update_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateOn;

    public Long getId() {
        return id;
    }

    public void setId(Long tvaEtMontantdeductibleBilanComptaId) {
        id = tvaEtMontantdeductibleBilanComptaId;
    }

    public Float getMontantHtAvecTauxDeduction() {
        return montantHtAvecTauxDeduction;
    }

    public void setMontantHtAvecTauxDeduction(Float montantHtAvecTauxDeduction) {
        this.montantHtAvecTauxDeduction = montantHtAvecTauxDeduction;
    }

    public Float getTVAAvecTauxDeduction() {
        return TVAAvecTauxDeduction;
    }

    public void setTVAAvecTauxDeduction(Float TVAAvecTauxDeduction) {
        this.TVAAvecTauxDeduction = TVAAvecTauxDeduction;
    }

    public CalculTVA getCalculTVA() {
        return calculTVA;
    }

    public void setCalculTVA(CalculTVA calculTVA) {
        this.calculTVA = calculTVA;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastUpdateOn() {
        return lastUpdateOn;
    }

    public void setLastUpdateOn(Date lastUpdateOn) {
        this.lastUpdateOn = lastUpdateOn;
    }

}
