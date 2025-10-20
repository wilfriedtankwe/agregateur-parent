package com.agregateur.dimsoft.agregateur_production.beans;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

/**
 * @author Severin dimo
 * @author Baudouin Meli
 */

@Entity
@Table(name = "periodetauxdeduction", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "category_id", "annee", "mois" }) })
@NamedQueries({ @NamedQuery(name = "PeriodeTauxDeduction.findAll", query = "SELECT p FROM PeriodeTauxDeduction p"),
        @NamedQuery(name = "PeriodeTauxDeduction.findByPeriodeTauxDeductionId", query = "SELECT p FROM PeriodeTauxDeduction p WHERE p.id = :id") })
public class PeriodeTauxDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    /**
     * Année de la période de facturation.
     */
    @Column(name = "annee")
    private String annee;

    /**
     * Mois de la période de facturation. Janvier = 0, Février = 1, etc.
     */
    @Column(name = "mois")
    private String mois;
    @Column(name = "taux")
    private Float taux;
    @Column(name = "sasu")
    Boolean sasu;
    @Column(name = "tva")
    Boolean tva;
    @Column(name = "tauxTva")
    private Float tauxTva;
    @ManyToOne
    private Category category;
    @Column(name = "nobatch")
    private Boolean nobatch;
    @Column(name = "cmt")
    private String cmt;
    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "last_update_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateOn;

    public Long getId() {
        return id;
    }

    public void setId(Long periodeTauxDeductionId) {
        this.id = periodeTauxDeductionId;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public Boolean getNobatch() {
        return nobatch;
    }

    public void setNobatch(Boolean nobatch) {
        this.nobatch = nobatch;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public Float getTaux() {
        return taux;
    }

    public void setTaux(Float taux) {
        this.taux = taux;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getSasu() {
        return sasu;
    }

    public void setSasu(Boolean sasu) {
        this.sasu = sasu;
    }

    public Boolean getTva() {
        return tva;
    }

    public void setTva(Boolean tva) {
        this.tva = tva;
    }

    public Float getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(Float tauxTva) {
        this.tauxTva = tauxTva;
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

    @Override
    public String toString() {
        return "PeriodeTauxDeduction{" + "id=" + id + ", annee='" + annee + '\'' + ", mois='" + mois + '\'' + ", taux="
                + taux + ", sasu=" + sasu + ", tva=" + tva + ", tauxTva=" + tauxTva + ", category=" + category + '}';
    }
}

