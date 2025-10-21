package com.agregateur.dimsoft.agregateur_production.factory;

import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.models.TransactionCADto;
import com.agregateur.dimsoft.agregateur_production.models.TransactionLCLDto;
import org.springframework.stereotype.Component;


@Component
public class BudgetFactoryImplement implements BudgetFactory {

    @Override
    public Budget createFromCA(TransactionCADto dto, Compte compte, Bank bank) {
        Budget budget = new Budget();
        budget.setCompte(compte);
        budget.setBank(bank);
        budget.setDateOperation(dto.getTransactionCADate());
        budget.setLibelle(dto.getTransactionCALibelle());
        if (dto.getTransactionCADebit() == null){
            budget.setMontant(dto.getTransactionCACredit());
        }else {
            budget.setMontant(dto.getTransactionCADebit());
        }
        return  budget;
    }

    @Override
    public Budget createFromLCL(TransactionLCLDto dto, Compte compte, Bank bank) {
        Budget budget = new Budget();
        budget.setCompte(compte);
        budget.setBank(bank);
        budget.setDateOperation(dto.getTransactionLCLDate());
        budget.setLibelle(dto.getTransactionLCLLibelle());
        budget.setMontant(dto.getTransactionLCLMontant());

        return  budget;
    }
}
