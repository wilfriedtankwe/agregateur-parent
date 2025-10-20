package com.agregateur.dimsoft.agregateur_production.services.impl;

import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.repositories.BudgetRepository;
import com.agregateur.dimsoft.agregateur_production.services.ImportFileService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class ImportFileServiceImpl implements ImportFileService {
    private final BudgetRepository repository;

    public ImportFileServiceImpl(BudgetRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public String agregate(List<Budget> budgets) {
        for (Budget b : budgets) {
            boolean exists = false;
            try {
                exists = repository.existsByDateOperationAndLibelleAndMontantAndCompteAndBank(
                        b.getDateOperation(), b.getLibelle(), b.getMontant(), b.getCompte(), b.getBank());
            } catch (Exception e) {
                // in case any field null, fallback to false
            }
            if (!exists) {
                repository.save(b);
            }
        }
        return "la copie s'est parfaitement deroulée";
    }
}
