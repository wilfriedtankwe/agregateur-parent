package com.agregateur.dimsoft.agregateur_production.factory;

import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.models.TransactionCADto;
import com.agregateur.dimsoft.agregateur_production.models.TransactionLCLDto;
import org.springframework.stereotype.Component;


@Component
public interface BudgetFactory {

    Budget createFromCA(TransactionCADto dto, Compte compte, Bank bank);
    Budget createFromLCL(TransactionLCLDto dto, Compte compte, Bank bank);

}
