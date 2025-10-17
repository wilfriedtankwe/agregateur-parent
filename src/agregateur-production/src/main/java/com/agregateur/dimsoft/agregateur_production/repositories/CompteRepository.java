package com.agregateur.dimsoft.agregateur_production.repositories;

import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte,Long> {
}
