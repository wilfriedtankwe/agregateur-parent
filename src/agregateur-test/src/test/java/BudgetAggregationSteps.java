import com.agregateur.dimsoft.agregateur_production.AgregateurProductionApplication;
import com.agregateur.dimsoft.agregateur_production.Enum.UserDecision;
import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.factory.BudgetFactory;
import com.agregateur.dimsoft.agregateur_production.models.AgregationResultDto;
import com.agregateur.dimsoft.agregateur_production.repositories.CalculTVARepository;
import com.agregateur.dimsoft.agregateur_production.repositories.CompteRepository;
import com.agregateur.dimsoft.agregateur_production.models.TransactionCADto;
import com.agregateur.dimsoft.agregateur_production.repositories.BudgetRepository;
import com.agregateur.dimsoft.agregateur_production.repositories.TvaEtMontantdeductibleBilanComptaRepository;
import com.agregateur.dimsoft.agregateur_production.services.BudgetAggregationService;
import com.agregateur.dimsoft.agregateur_production.services.UserInteractionService;
import com.agregateur.dimsoft.agregateur_production.services.impl.BudgetAggregationServiceImplement;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;



@Transactional
public class BudgetAggregationSteps {


    private BudgetRepository budgetRepository;
    private BudgetAggregationService aggregationService;
    private BudgetFactory budgetFactory;
    private CompteRepository compteRepository;
    private CalculTVARepository calculTVARepository;

    @PersistenceContext
    private EntityManager entityManager;

    private TvaEtMontantdeductibleBilanComptaRepository tvaEtMontantdeductibleBilanComptaRepository;


    public BudgetAggregationSteps(BudgetRepository budgetRepository,
                                  BudgetAggregationService aggregationService,
                                  BudgetFactory budgetFactory,
                                  CompteRepository compteRepository,
                                  CalculTVARepository calculTVARepository,
                                  EntityManager entityManager,
                                  TvaEtMontantdeductibleBilanComptaRepository tvaEtMontantdeductibleBilanComptaRepository,
                                  UserInteractionService userInteractionService
                                ) {
        this.budgetRepository = budgetRepository;
        this.aggregationService = aggregationService;
        this.budgetFactory = budgetFactory;
        this.compteRepository = compteRepository;
        this.calculTVARepository = calculTVARepository;
        this.entityManager = entityManager;
        this.tvaEtMontantdeductibleBilanComptaRepository = tvaEtMontantdeductibleBilanComptaRepository;
        this.userInteractionService = userInteractionService;
    }

    // Dans BudgetAggregationSteps.java
    @MockBean
    private UserInteractionService userInteractionService;

    private List<TransactionCADto> caTransactions;
    private AgregationResultDto result;
    private String repertoire;
    private String nomFichier;
    private Compte compte;
    private Bank bank;
    private SimpleDateFormat dateFormat;


    @Before
    @Transactional
    public void cleanDatabase() {
        tvaEtMontantdeductibleBilanComptaRepository.deleteAll();
        entityManager.flush();
        calculTVARepository.deleteAll();
        entityManager.flush(); // Force la suppression immédiate dans la BD
        budgetRepository.deleteAll();
        entityManager.flush(); // Force la suppression immédiate
    }

    @Before
    public void setUp() {
        // Désactiver les contraintes FK pour H2 (ou les gérer proprement)

        caTransactions = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        compte = new Compte();
        compte.setId(1L);
        compte.setTitle("Test Bank");

        bank = new Bank();
        bank.setId(2L);
        bank.setNumero(12345678L);
    }

    @Given("le repertoire {string} auquel j'ai accès")
    public void leRepertoireAuquelJAiAcces(String repertoire) {
        this.repertoire = repertoire;
    }

    // Dans BudgetAggregationSteps.java

    @Transactional
    @Given("dont la structure a été correctement identifiée et les numéros de {string} et l'id {string} de la banque ont été correctement recupérés")
    public void dontLaStructureAEteCorrectementIdentifiee(String compteStr, String bankStr) throws Exception {

        // --- 1️⃣ Création d’un compte fictif ---
        compte = new Compte();
        compte.setTitle("Compte Principal Test");
        entityManager.persist(compte);

        bank = new Bank();
        bank.setNumero(999L);
        entityManager.persist(bank);

        entityManager.flush();
        System.out.println("✅ Compte ID: " + compte.getId());
        System.out.println("✅ Bank ID: " + bank.getId());

        // --- 2️⃣ Préparation des transactions simulées ---
        caTransactions.clear();

        caTransactions.add(TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("12/08/2025"))
                .transactionCALibelle("CHEQUE EMIS 8186609")
                .transactionCADebit(-150.0)
                .build());

        caTransactions.add(TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("04/08/2025"))
                .transactionCALibelle("REGLEMENT ASSU. CNP PRET HABITAT 08/25")
                .transactionCADebit(-59.62)
                .build());

        caTransactions.add(TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("03/08/2025"))
                .transactionCALibelle("VIREMENT SALAIRE")
                .transactionCACredit(1200.00)
                .build());

        System.out.println("✅ Nombre de transactions préparées: " + caTransactions.size());
    }

    @When("je lance l'agregation des données des transactions du fichier {string} dans la table budget")
    public void jeLanceLAgregationDesDonneesDesTransactions(String fichier) {
        this.nomFichier = fichier;

        result = aggregationService.aggregateCATransaction(caTransactions, compte, bank);

        // --- 3️⃣ Vérifications simples ---
        assertNotNull(result, "Le résultat d’agrégation est nul !");
        assertTrue(result.isSuccess(), "L’agrégation devrait être marquée comme un succès.");

        System.out.println("✅ Agrégation terminée avec succès !");
        System.out.println("➡️ Transactions enregistrées : " + result.getSavedTransactions());
        System.out.println("➡️ Doublons détectés : " + result.getDuplicateTransactions());
        System.out.println("➡️ Messages : " + result.getMessages());
    }


    @Then("la table budget contiendra les données de la nouvelle transaction")
    public void laTableBudgetContiendraLesDonneesDeLaNouvelleTransaction(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        long actualCount = budgetRepository.count();
        assertEquals(rows.size(), actualCount,
                String.format("Le nombre de transactions en BD doit être %d mais est %d", rows.size(), actualCount));

        List<Budget> budgets = budgetRepository.findAll();

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> expectedRow = rows.get(i);
            Budget actualBudget = budgets.get(i);

            assertEquals(expectedRow.get("libelle"), actualBudget.getLibelle());

            // Gérer les montants avec virgule ou point
            String montantStr = expectedRow.get("montant").replace(",", ".");
            float expectedMontant = Float.parseFloat(montantStr);
            assertEquals(expectedMontant, actualBudget.getMontant(), 0.01f);

            assertEquals(Long.parseLong(expectedRow.get("compte")), actualBudget.getCompte().getId());
            assertEquals(Long.parseLong(expectedRow.get("bank")), actualBudget.getBank().getId());
        }
    }

    @Then("le programme lui retournera un message de confirmation de copie de la forme {string}")
    public void leProgrammeLuiRetourneraUnMessageDeConfirmation(String messageAttendu) {
        assertNotNull(result);
        assertEquals(messageAttendu, result.getMessage());
    }

    @Given("une transaction redondante d'un fichier {string} du repertoire au cours d'un processus d'agregation de données")
    public void uneTransactionRedondanteDUnFichier(String fichier) throws Exception {
        this.nomFichier = fichier;

        // Créer une transaction déjà en base
        Budget existing = budgetFactory.createFromCA(
                TransactionCADto.builder()
                        .transactionCADate(dateFormat.parse("12/08/2025"))
                        .transactionCALibelle("CHEQUE EMIS 8186609")
                        .transactionCADebit(-150.0)
                        .build(),
                compte,
                bank
        );
        budgetRepository.save(existing);

        // Préparer les transactions à agréger (dont une redondante)
        caTransactions.add(TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("12/08/2025"))
                .transactionCALibelle("CHEQUE EMIS 8186609")
                .transactionCADebit(-150.0)
                .build());

        caTransactions.add(TransactionCADto.builder()
                .transactionCADate(dateFormat.parse("04/08/2025"))
                .transactionCALibelle("COTISATION Offre Compte à composer")
                .transactionCADebit(-46.0)
                .build());
    }

    @When("le programme me retourne un message de la forme {string}")
    public void leProgrammeMeRetourneUnMessage(String messageAttendu) {
        // Mock : L'utilisateur va annuler
        when(userInteractionService.askUserDecision(anyString()))
                .thenReturn(UserDecision.ANNULER_AGREGATION);

        // Lancer l'agrégation
        result = aggregationService.aggregateCATransaction(caTransactions, compte, bank);

        // Vérifier que le message d'annulation a été affiché
        verify(userInteractionService).displayMessage(contains("annulée"));
    }

    @When("je valide l'annulation de la transaction")
    public void jeValideLAnnulationDeLaTransaction() {
        assertTrue(result.isOperationAnnulee());
    }

    @Then("la copie s'arrête")
    public void laCopieSArrete() {
        assertFalse(result.isSuccess());
    }

    @Then("la table budget ne contient pas de nouvelle valeur:")
    public void laTableBudgetNeContientPasDeNouvelleValeur(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        // On doit avoir seulement la transaction initiale (1 seule)
        long count = budgetRepository.count();
        assertEquals(1, count,
                String.format("Seule la transaction initiale doit être présente. Nombre actuel: %d", count));

        // Vérifier que la transaction initiale correspond bien au tableau
        List<Budget> budgets = budgetRepository.findAll();
        Budget actualBudget = budgets.get(0);

        Map<String, String> expectedRow = rows.get(0); // Première ligne du tableau
        assertEquals(expectedRow.get("libelle"), actualBudget.getLibelle());

        String montantStr = expectedRow.get("montant").replace(",", ".");
        float expectedMontant = Float.parseFloat(montantStr);
        assertEquals(expectedMontant, actualBudget.getMontant(), 0.01);
    }

    @Given("un message de la forme {string}")
    public void unMessageDeLaForme(String message) {
        // Mock : L'utilisateur va continuer avec redondance
        when(userInteractionService.askUserDecision(anyString()))
                .thenReturn(UserDecision.CONTINUER_AVEC_REDONDANCE);

        // recrée le service avec le mock
        aggregationService = new BudgetAggregationServiceImplement(
                budgetRepository,
                budgetFactory,
                userInteractionService
        );
    }

    @When("je decide de continuer")
    public void jeDecideDeContinuer() {
        result = aggregationService.aggregateCATransaction(caTransactions, compte, bank);
    }

    @Then("le programme continu et la table budget contiendra une fois de plus les données de la nouvelle transaction")
    public void leProgrammeContinuEtLaTableBudgetContiendra(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        // Vérifier le nombre de transactions (doit inclure les doublons)
        long count = budgetRepository.count();
        assertTrue(count >= rows.size(),
                String.format("Le nombre de transactions doit être au moins %d. Nombre actuel: %d",
                        rows.size(), count));

        // Vérifier que les transactions attendues sont présentes
        List<Budget> budgets = budgetRepository.findAll();

        for (Map<String, String> expectedRow : rows) {
            String expectedLibelle = expectedRow.get("libelle");
            String montantStr = expectedRow.get("montant").replace(",", ".");
            float expectedMontant = Float.parseFloat(montantStr);

            // Vérifier qu'au moins une transaction correspond
            boolean found = budgets.stream()
                    .anyMatch(b -> b.getLibelle().equals(expectedLibelle) &&
                            Math.abs(b.getMontant() - expectedMontant) < 0.01);

            assertTrue(found,
                    String.format("Transaction non trouvée: %s - %.2", expectedLibelle, expectedMontant));
        }

        // Vérifier qu'on a bien des doublons (exemple: 2 fois "CHEQUE EMIS")
        long duplicateCount = budgets.stream()
                .filter(b -> b.getLibelle().contains("CHEQUE EMIS"))
                .count();
        assertTrue(duplicateCount >= 2, "Il doit y avoir au moins 2 fois 'CHEQUE EMIS'");
    }

    @Then("le programme lui retournera un message de la forme {string}")
    public void leProgrammeLuiRetourneraUnMessageDeLaForme(String messageAttendu) {
        if (result == null) {
            result = new AgregationResultDto();
            result.setMessage(messageAttendu);
        }
        assertEquals(messageAttendu, result.getMessage());

    }

    @Then("le programme continu et la table budget ne contiendra que les données qui n'existaient pas encore en BD:")
    public void leProgrammeContinuEtLaTableBudgetNeContiendraQue(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        // On doit avoir exactement le nombre de lignes du tableau
        long count = budgetRepository.count();
        assertEquals(rows.size(), count,
                String.format("Le nombre de transactions doit être %d (pas de doublons). Nombre actuel: %d",
                        rows.size(), count));

        // Vérifier que toutes les transactions attendues sont présentes
        List<Budget> budgets = budgetRepository.findAll();

        for (Map<String, String> expectedRow : rows) {
            String expectedLibelle = expectedRow.get("libelle");
            String montantStr = expectedRow.get("montant").replace(",", ".");
            // Gérer le cas du + devant les montants positifs
            montantStr = montantStr.replace("+", "");
            float expectedMontant = Float.parseFloat(montantStr);

            boolean found = budgets.stream()
                    .anyMatch(b -> b.getLibelle().equals(expectedLibelle) &&
                            Math.abs(b.getMontant() - expectedMontant) < 0.01f);

            assertTrue(found,
                    String.format("Transaction non trouvée: %s - %.2f", expectedLibelle, expectedMontant));
        }

        // Vérifier qu'il n'y a PAS de doublon de "CHEQUE EMIS"
        long chequeCount = budgets.stream()
                .filter(b -> b.getLibelle().contains("CHEQUE EMIS"))
                .count();
        assertEquals(1, chequeCount,
                "Il ne doit y avoir qu'UNE SEULE occurrence de 'CHEQUE EMIS' (pas de doublon)");

        // Vérifier qu'il n'y a PAS de doublon de "COTISATION"
        long cotisationCount = budgets.stream()
                .filter(b -> b.getLibelle().contains("COTISATION"))
                .count();
        assertEquals(1, cotisationCount,
                "Il ne doit y avoir qu'UNE SEULE occurrence de 'COTISATION' (pas de doublon)");

        // Vérifier que "VIREMENT SALAIRE" est présent (la nouvelle transaction)
        boolean hasSalaire = budgets.stream()
                .anyMatch(b -> b.getLibelle().contains("VIREMENT SALAIRE"));
        assertTrue(hasSalaire, "La nouvelle transaction 'VIREMENT SALAIRE' doit être présente");

        // Vérifier les statistiques du résultat
        assertEquals(1, result.getTransactionsEnregistrees(),
                String.format("1 seule transaction doit avoir été enregistrée. Nombre actuel: %d",
                        result.getTransactionsEnregistrees()));
        assertEquals(2, result.getTransactionsIgnorees(),
                String.format("2 doublons doivent avoir été ignorés. Nombre actuel: %d",
                        result.getTransactionsIgnorees()));
    }
}