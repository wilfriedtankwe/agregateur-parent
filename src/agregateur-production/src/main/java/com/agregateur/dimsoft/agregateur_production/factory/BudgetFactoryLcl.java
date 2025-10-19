package com.agregateur.dimsoft.agregateur_production.factory;

import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.repositories.BankRepository;
import com.agregateur.dimsoft.agregateur_production.repositories.CompteRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BudgetFactoryLcl {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static BankRepository bankRepository;
    public static CompteRepository compteRepository;
    public BudgetFactoryLcl(BankRepository bankRepository,CompteRepository compteRepository){
        BudgetFactoryLcl.bankRepository = bankRepository;
        BudgetFactoryLcl.compteRepository = compteRepository;
    }

    public static Budget fromCsv(String[] cols) {
        Budget b = new Budget();

        // Defensive access
        String dateStr = cols.length > 0 ? cols[0].trim() : "";
        String montantStr = cols.length > 1 ? cols[1].trim() : "";
        String typeOperation = cols.length > 2 ? cols[2].trim() : "";
        String libelle = ""; // try to guess from later columns
        if (cols.length > 3) {
            // some LCL lines put description at index 3 or later
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < cols.length; i++) {
                if (cols[i] != null && !cols[i].trim().isEmpty()) {
                    if (sb.length() > 0) sb.append(" ; ");
                    sb.append(cols[i].trim());
                }
            }
            libelle = sb.toString();
        }

        if (!dateStr.isEmpty()) {
            try {
                LocalDate d = LocalDate.parse(dateStr, DATE_FMT);
                b.setDateOperation(d);
            } catch (Exception ex) {
                // leave null or handle logging
            }
        }

        b.setLibelle(libelle.trim().isEmpty() ? typeOperation : libelle);
        if (!montantStr.isEmpty()) {
            try {
                Float montant = new Float(montantStr.replace(",", ".").replaceAll("\\s+", ""));
                b.setMontant(montant);
            } catch (Exception ex) {
                // ignore parse error
            }
        }
        Compte defaultCompte= compteRepository.findById(4l).get();
        Bank defautCompte=bankRepository.findById(3l).get();

        // Default values — adjust if you have compte/bank
        b.setCompte(defaultCompte);
        b.setBank(defautCompte);
        return b;
    }
}
