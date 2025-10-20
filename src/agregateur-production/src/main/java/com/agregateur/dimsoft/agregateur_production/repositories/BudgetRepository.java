package com.agregateur.dimsoft.agregateur_production.repositories;

import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {

    @Query("SELECT b FROM Budget b WHERE b.dateOperation = :date " +
            "AND TRIM(b.libelle) = TRIM(:libelle) " +
            "AND b.montant = :montant")
    Optional<Budget> findDuplicateTransaction(
            @Param("date") Date dateOperation,
            @Param("libelle") String libelle,
            @Param("montant") Double montant
    );

}
