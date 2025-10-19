package com.agregateur.dimsoft.agregateur_production.factory;

import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.repositories.BankRepository;
import com.agregateur.dimsoft.agregateur_production.repositories.CompteRepository;
import com.agregateur.dimsoft.agregateur_production.util.CsvHeaderMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BudgetFactoryCa {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static BankRepository bankRepository;
    public static CompteRepository compteRepository;

    public BudgetFactoryCa(BankRepository bankRepository, CompteRepository compteRepository) {
        BudgetFactoryCa.bankRepository = bankRepository;
        BudgetFactoryCa.compteRepository = compteRepository;
    }


    public static Budget fromCsv(String[] cols, CsvHeaderMapping mapping) {
        Budget b = new Budget();

        // Try to find date column
        int dateIdx = mapping.indexOf("DATE");
        int libelleIdx = mapping.indexOf("LIBEL", "LIBELLÉ", "LIBELLE");
        int debitIdx = mapping.indexOf("DEBIT", "DEBIT EURO", "DEBIT EURO");
        int creditIdx = mapping.indexOf("CREDIT", "CREDIT EURO", "CREDIT EURO");
        int montantIdx = debitIdx != -1 ? debitIdx : creditIdx;

        if (dateIdx != -1 && dateIdx < cols.length) {
            String d = cols[dateIdx].trim();
            try {
                b.setDateOperation(LocalDate.parse(d, DATE_FMT));
            } catch (Exception ex) { /* ignore */ }
        }

        if (libelleIdx != -1 && libelleIdx < cols.length) {
            b.setLibelle(cols[libelleIdx].trim());
        } else {
            // try fallback: some CA files put description after date and amount
            if (cols.length > 2) b.setLibelle(cols[2].trim());
        }

        if (montantIdx != -1 && montantIdx < cols.length) {
            String m = cols[montantIdx].trim();
            try {
                Float montant = new Float(m.replace(",", ".").replaceAll("\\s+", ""));
                b.setMontant(montant);
            } catch (Exception ex) { /* ignore */ }
        }
        Compte defaultCompte = compteRepository.findById(1l).get();
        Bank defaultBank = bankRepository.findById(2L).get();


        b.setCompte(defaultCompte);
        b.setBank(defaultBank);
        return b;
    }
}
