package com.agregateur.dimsoft.agregateur_production.factory.factoryTest;


import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.factory.BudgetFactory;
import com.agregateur.dimsoft.agregateur_production.factory.BudgetFactoryImplement;
import com.agregateur.dimsoft.agregateur_production.models.TransactionCADto;
import com.agregateur.dimsoft.agregateur_production.models.TransactionLCLDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour BudgetFactory
 *
 * Objectif : Vérifier que la Factory crée correctement les entités Budget
 * à partir des DTOs CA et LCL
 */
class BudgetFactoryTest {

    private BudgetFactory factory;
    private Compte compte;
    private Bank bank;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        factory = new BudgetFactoryImplement();

        // Format de date utilisé dans les fichiers
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Créer un compte de test
        compte = new Compte();
        compte.setId(1L);
        compte.setTitle("123456789");

        // Créer une banque de test
        bank = new Bank();
        bank.setId(2L);
        bank.setNumero(012345L);
    }


    @Test
    @DisplayName("Doit créer un Budget avec montant NÉGATIF à partir d'un débit CA")
    void shouldCreateBudgetFromCAWithDebit() throws ParseException {
        // Given : Un DTO CA avec un débit
        TransactionCADto dto = TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("12/08/2025"))
                .transactionCALibelle("CHEQUE EMIS 8186609")
                .transactionCADebit(-150.0)
                .transactionCACredit(null)
                .build();

        // When : On crée le Budget via la Factory
        Budget budget = factory.createFromCA(dto, compte, bank);

        // Then : Vérifications
        assertNotNull(budget, "Le budget ne doit pas être null");

        // Vérifier les champs de base
        assertEquals(dateFormat.parse("12/08/2025"), budget.getDateOperation());
        assertEquals("CHEQUE EMIS 8186609", budget.getLibelle());
        assertEquals(-150.0, budget.getMontant(), "Le débit doit être converti en montant NÉGATIF");

        // Vérifier les relations
        assertEquals(compte, budget.getCompte());
        assertEquals(bank, budget.getBank());

        // Vérifier les champs techniques
        assertNotNull(budget.getCreatedOn(), "createdOn doit être initialisé");
        assertNotNull(budget.getLastUpdateOn(), "lastUpdateOn doit être initialisé");
        assertFalse(budget.getIsDeleted(), "isDeleted doit être false par défaut");

        // Vérifier les booléens métier
        assertFalse(budget.gettransactionCATvaPaid());
        assertFalse(budget.getSasu());
        assertFalse(budget.getNobatch());
        assertFalse(budget.getValfact());
    }

    @Test
    @DisplayName("Doit créer un Budget avec montant POSITIF à partir d'un crédit CA")
    void shouldCreateBudgetFromCAWithCredit() throws ParseException {
        // Given : Un DTO CA avec un crédit
        TransactionCADto dto = TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("08/08/2025"))
                .transactionCALibelle("VIREMENT EN VOTRE FAVEUR VIR INST de M SEVERIN DIMO NOULA")
                .transactionCACredit(null)
                .transactionCADebit((double) -1100L)
                .build();

        // When
        Budget budget = factory.createFromCA(dto, compte, bank);

        // Then
        assertNotNull(budget);
        assertEquals(1100f, budget.getMontant(), "Le crédit doit être converti en montant POSITIF");
        assertEquals("VIREMENT EN VOTRE FAVEUR VIR INST de M SEVERIN DIMO NOULA", budget.getLibelle());
    }

    @Test
    @DisplayName("Doit retourner montant=0 si ni débit ni crédit dans CA")
    void shouldReturnZeroMontantWhenNoDebitNorCreditInCA() throws ParseException {
        // Given : Un DTO CA sans débit ni crédit
        TransactionCADto dto = TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("04/08/2025"))
                .transactionCALibelle("COTISATION Offre Compte à composer")
                .transactionCADebit(null)
                .transactionCACredit(null)
                .build();

        // When
        Budget budget = factory.createFromCA(dto, compte, bank);

        // Then
        assertEquals(0f, budget.getMontant(), "Le montant doit être 0 si ni débit ni crédit");
    }

    @Test
    @DisplayName("Doit donner priorité au crédit si débit ET crédit sont présents dans CA")
    void shouldPrioritizeCreditOverDebitInCA() throws ParseException {
        // Given : Un DTO CA avec débit ET crédit (cas anormal mais on teste la robustesse)
        TransactionCADto dto = TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("04/08/2025"))
                .transactionCALibelle("Transaction bizarre")
                .transactionCADebit((double) -50L)
                .transactionCACredit((double) 100L) // Les deux présents
                .build();

        // When
        Budget budget = factory.createFromCA(dto, compte, bank);

        // Then
        assertEquals(100L, budget.getMontant(), "Le crédit doit avoir la priorité");
    }



    @Test
    @DisplayName("Doit créer un Budget à partir d'un DTO LCL avec montant positif")
    void shouldCreateBudgetFromLCLWithPositiveMontant() throws ParseException {
        // Given : Un DTO LCL avec un montant positif (crédit)
        TransactionLCLDto dto = TransactionLCLDto.builder()
                .transactionLCLDate(dateFormat.parse("03/06/2025"))
                .transactionLCLMontant(33.54)
                .transactionLCLLibelle("VIREMENT Mutuelle Complementair")
                .build();

        // When
        Budget budget = factory.createFromLCL(dto, compte, bank);

        // Then
        assertNotNull(budget);
        assertEquals(dateFormat.parse("03/06/2025"), budget.getDateOperation());
        assertEquals("VIREMENT Mutuelle Complementair", budget.getLibelle());
        assertEquals(33.54, budget.getMontant(), "Le montant LCL doit être conservé tel quel");
        assertEquals(compte, budget.getCompte());
        assertEquals(bank, budget.getBank());
    }

    @Test
    @DisplayName("Doit créer un Budget à partir d'un DTO LCL avec montant négatif")
    void shouldCreateBudgetFromLCLWithNegativeMontant() throws ParseException {
        // Given : Un DTO LCL avec un montant négatif (débit)
        TransactionLCLDto dto = TransactionLCLDto.builder()
                .transactionLCLDate(dateFormat.parse("04/06/2025"))
                .transactionLCLMontant(-48.83)
                .transactionLCLLibelle("Klarna*SHEIN")
                .build();

        // When
        Budget budget = factory.createFromLCL(dto, compte, bank);

        // Then
        assertEquals(-48.83, budget.getMontant(), "Le montant négatif LCL doit être conservé");
        assertEquals("Klarna*SHEIN", budget.getLibelle());
    }

    @Test
    @DisplayName("Doit créer un Budget LCL avec montant=0")
    void shouldCreateBudgetFromLCLWithZeroMontant() throws ParseException {
        // Given : Un DTO LCL avec montant=0
        TransactionLCLDto dto = TransactionLCLDto.builder()
                .transactionLCLDate(dateFormat.parse("05/06/2025"))
                .transactionLCLMontant((double) 0)
                .transactionLCLLibelle("Opération neutre")
                .build();

        // When
        Budget budget = factory.createFromLCL(dto, compte, bank);

        // Then
        assertEquals(0, budget.getMontant());
    }


    @Test
    @DisplayName("Doit initialiser les champs techniques pour CA et LCL")
    void shouldInitializeTechnicalFieldsForBothTypes() throws ParseException {
        // Given : Des DTOs CA et LCL
        TransactionCADto caDto = TransactionCADto.builder()
                .transactionCADate(new Date())
                .transactionCALibelle("Test CA")
                .transactionCACredit(10.0)
                .build();

        TransactionLCLDto lclDto = TransactionLCLDto.builder()
                .transactionLCLDate(new Date())
                .transactionLCLLibelle("Test LCL")
                .transactionLCLMontant((double) -10)
                .build();

        // When
        Budget budgetCA = factory.createFromCA(caDto, compte, bank);
        Budget budgetLCL = factory.createFromLCL(lclDto, compte, bank);

        // Then : Vérifier que les deux ont les champs techniques initialisés
        assertNotNull(budgetCA.getCreatedOn());
        assertNotNull(budgetCA.getLastUpdateOn());
        assertFalse(budgetCA.getIsDeleted());

        assertNotNull(budgetLCL.getCreatedOn());
        assertNotNull(budgetLCL.getLastUpdateOn());
        assertFalse(budgetLCL.getIsDeleted());
    }

    @Test
    @DisplayName("Doit conserver le même Compte et Bank pour CA et LCL")
    void shouldKeepSameCompteAndBankReference() throws ParseException {
        // Given
        TransactionCADto caDto = TransactionCADto.builder()
                .transactionCADate(new Date())
                .transactionCALibelle("Test")
                .transactionCADebit(10.0)
                .build();

        // When
        Budget budget = factory.createFromCA(caDto, compte, bank);

        // Then : Vérifier que ce sont les MÊMES instances (pas des copies)
        assertSame(compte, budget.getCompte(), "Doit être la même instance de Compte");
        assertSame(bank, budget.getBank(), "Doit être la même instance de Bank");
    }
}
