package com.agregateur.dimsoft.agregateur_production.repositories;

import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {

}
