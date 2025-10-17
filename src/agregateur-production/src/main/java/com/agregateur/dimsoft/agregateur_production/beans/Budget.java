package com.agregateur.dimsoft.agregateur_production.beans;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Severin dimo
 * @author Baudouin Meli
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "numero")
    private String numero;
    @Column(name = "dateOperation")
    private Date dateOperation;
    @Column(name = "libelle")
    private String libelle;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "budget_subcategory", joinColumns = @JoinColumn(name = "budget_id"), inverseJoinColumns = @JoinColumn(name = "subcategory_id"))
    private Set<SubCategory> subCategories = new HashSet<>();
    @Column(name = "montant")
    private Float montant;
    @Column(name = "tva_paid")
    private Boolean isTvaPaid;
    @Column(name = "notes")
    private String notes;
    @Column(name = "chequeNumero")
    private String chequeNumero;
    @ManyToOne
    private Compte compte;
    @ManyToOne
    private Bank bank;
    @OneToOne
    private Category category;
    @Column(name = "factimg")
    private String factimg;
    @OneToOne
    private Budget parent;
    @Column(name = "sasu")
    private Boolean sasu;
    @Column(name = "nobatch")
    private Boolean nobatch;
    @Column(name = "valfact")
    private Boolean valfact;
    @Column(name = "prevalfact")
    private Boolean prevalfact;
    @Column(name = "toprevalfact")
    private Boolean toprevalfact;
    @Column(name = "immo")
    private Boolean immo;
    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Column(name = "last_update_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateOn;
    @Column(name = "factureId")
    private String factureId;
    @Column(name = "tva")
    private Boolean tva;

    @Column(name = "supprimer")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "pret_id", nullable = true)
    @JsonProperty(access = Access.WRITE_ONLY)
    private Pret pret;

    @PrePersist
    public void onPrePersist() {
        performBilanPostOperation();
    }

    @PreUpdate
    public void onPreUpdate() {
        performBilanPostOperation();
    }

    private void performBilanPostOperation() {

        if (this.prevalfact != null && this.prevalfact) {
            this.nobatch = this.nobatch != null ? false : true;
            this.sasu = true;
            this.tva = true;
        }
        if (this.valfact != null && this.valfact) {
            this.nobatch = this.nobatch != null && !this.nobatch ? false : true;
            this.sasu = true;
            this.tva = true;
        }

    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsDeleted() {
        return isDeleted != null ? isDeleted : false;
    }

    @Override
    public String toString() {
        return "Budget{" + "id=" + id + ", dateOperation=" + dateOperation + ", libelle='" + libelle + '\''
                + ", subCategories=" + subCategories + ", montant=" + montant + ", notes='" + notes + '\''
                + ", chequeNumero='" + chequeNumero + '\'' + ", compte=" + compte + ", bank=" + bank + ", category="
                + category + ", factimg='" + factimg + '\'' + ", parent=" + parent + ", sasu=" + sasu + ", nobatch="
                + nobatch + ", valfact=" + valfact + ", prevalfact=" + prevalfact + ", toprevalfact=" + toprevalfact
                + ", tva=" + tva + '}';
    }

}
