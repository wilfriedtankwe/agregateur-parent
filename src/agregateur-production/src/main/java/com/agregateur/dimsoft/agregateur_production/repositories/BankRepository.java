package com.agregateur.dimsoft.agregateur_production.repositories;

import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank,Long> {
}
