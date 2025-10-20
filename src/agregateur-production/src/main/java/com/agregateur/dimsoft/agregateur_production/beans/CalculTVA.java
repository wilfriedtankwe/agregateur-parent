package com.agregateur.dimsoft.agregateur_production.beans;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "calcultva")
@NamedQueries({ @NamedQuery(name = "CalculTVA.findAll", query = "SELECT c FROM CalculTVA c"),
        @NamedQuery(name = "CalculTVA.findById", query = "SELECT c FROM CalculTVA c WHERE c.id = :id") })
public class CalculTVA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "TVASansTauxAppliqueFromIHM")
    private Float TVASansTauxAppliqueFromIHM;
    @Column(name = "tauxTva")
    private Float tauxTva;
    @OneToOne(cascade = CascadeType.MERGE)
    private Budget budget;
    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "last_update_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateOn;

    @OneToOne
    private PeriodeTauxDeduction periodeTauxDeduction; // finder from TauxDeduction=f(budget.category) when setting

    public Float getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(Float tauxTva) {
        this.tauxTva = tauxTva;
    }

    public Float getTVASansTauxAppliqueFromIHM() {
        return TVASansTauxAppliqueFromIHM;
    }

    public void setTVASansTauxAppliqueFromIHM(Float TVASansTauxAppliqueFromIHM) {
        this.TVASansTauxAppliqueFromIHM = TVASansTauxAppliqueFromIHM;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public PeriodeTauxDeduction getPeriodeTauxDeduction() {
        return periodeTauxDeduction;
    }

    public void setPeriodeTauxDeduction(PeriodeTauxDeduction periodeTauxDeduction) {
        this.periodeTauxDeduction = periodeTauxDeduction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long calcultvaId) {
        this.id = calcultvaId;
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
