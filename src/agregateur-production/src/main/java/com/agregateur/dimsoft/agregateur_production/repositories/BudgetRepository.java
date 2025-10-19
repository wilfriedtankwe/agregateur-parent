package com.agregateur.dimsoft.agregateur_production.repositories;

import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {
    boolean existsByDateOperationAndLibelleAndMontantAndCompteAndBank(
            LocalDate  dateOperation,
            String libelle,
            Float montant,
            Compte compte,
            Bank bank
    );

}
