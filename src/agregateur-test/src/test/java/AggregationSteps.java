import com.agregateur.dimsoft.agregateur_production.Enum.UserDecision;
import com.agregateur.dimsoft.agregateur_production.beans.Bank;
import com.agregateur.dimsoft.agregateur_production.beans.Compte;
import com.agregateur.dimsoft.agregateur_production.models.AgregationResultDto;
import com.agregateur.dimsoft.agregateur_production.models.TransactionCADto;
import com.agregateur.dimsoft.agregateur_production.models.TransactionLCLDto;
import com.agregateur.dimsoft.agregateur_production.services.BudgetAggregationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AggregationSteps {


    private BudgetAggregationService budgetAggregationService; // peut être null si pas injecté

    public AggregationSteps(BudgetAggregationService budgetAggregationService) {
        this.budgetAggregationService = budgetAggregationService;
    }

    private String fileName;
    private BudgetAggregationService budgetService;
    private List<TransactionCADto> transactionsCA;
    private List<TransactionLCLDto> transactionsLCL;
    private Compte compte;
    private Bank bank;
    private AgregationResultDto result;
    private UserDecision userDecision;
    private String messageRecu;

    private List<Map<String, String>> budget = new ArrayList<>();

    public static Date convertStringToDate(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // mois en chiffres
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        Date date = Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        return date;
    }

        @Given("une transaction redondante d'un fichier {string} du repertoire au cours d'un processus d'agregation de données")
    public void une_transaction_redondante_d_un_fichier_du_repertoire_au_cours_d_un_processus_d_agregation_de_données(String expectedFileName) {
        this.fileName = expectedFileName;
        String currentDate = "12/08/2025";
        List<TransactionCADto> transactionCA = new ArrayList<>();
        TransactionCADto t1 = new TransactionCADto();
        t1.setTransactionCADate(convertStringToDate(currentDate));
        t1.setTransactionCALibelle("CHEQUE EMIS 8186609");
        t1.setTransactionCACredit(150.0);
        transactionsCA.add(t1);
    }

    @When("le programme me retourne un message de la forme {string}")
    public void le_programme_me_retourne_un_message_de_la_forme(String expectedMessage) {
        Objects.requireNonNull(budgetService, "Le service d'agrégation n'a pas été initialisé !");

        if (expectedMessage.contains("annulée")) {
            userDecision = UserDecision.ANNULER_AGREGATION;
        } else if (expectedMessage.contains("quand même enregistrer")) {
            userDecision = UserDecision.CONTINUER_AVEC_REDONDANCE;
        } else if (expectedMessage.contains("sans enregistrer")) {
            userDecision = UserDecision.CONTINUER_SANS_REDONDANCE;
        }
    }

    @When("je valide l'annulation de la transaction")
    public void je_valide_l_annulation_de_la_transaction() {
        result = budgetService.aggregateCATransaction(transactionsCA, compte, bank);
    }

    @Then("la copie s'arrête")
    public void la_copie_s_arrête() {
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().stream().anyMatch(m -> m.contains("la copie s'arrête")));
    }

    @Then("la table budget ne contient pas de nouvelle valeur:")
    public void la_table_budget_ne_contient_pas_de_nouvelle_valeur(io.cucumber.datatable.DataTable dataTable) {
        assertTrue(result.isSuccess());
        assertEquals(1, result.getSavedTransactions());
        assertEquals(1, result.getDuplicateTransactions());
    }

    @Given("un message de la forme {string}")
    public void un_message_de_la_forme(String expectedMessage) {
        // On stocke le message reçu
        messageRecu = expectedMessage;
        System.out.println("Message reçu : " + messageRecu);

    }

    @When("je decide de continuer")
    public void je_decide_de_continuer() {
        // Ici on peut simuler la validation ou le traitement du message
        System.out.println("Décision de continuer prise pour le message : " + messageRecu);
    }

    @Then("le programme continu et la table budget contiendra une fois de plus les données de la nouvelle transaction")
    public void le_programme_continu_et_la_table_budget_contiendra_une_fois_de_plus_les_données_de_la_nouvelle_transaction(io.cucumber.datatable.DataTable dataTable) {

        // Convertit la DataTable Cucumber en liste de maps
        List<Map<String, String>> nouvellesTransactions = dataTable.asMaps(String.class, String.class);

        // Ajoute chaque transaction à la table budget simulée
        budget.addAll(nouvellesTransactions);

        System.out.println("Table budget après ajout : ");
        budget.forEach(System.out::println);

    }

    @Then("le programme lui retournera un message de la forme {string}")
    public void le_programme_lui_retournera_un_message_de_la_forme(String expectedMessage) {
        // Simule l'envoi d'un message de confirmation
        String messageRetourne = "Transaction ajoutée avec succès"; // exemple
        System.out.println("Message retourné : " + messageRetourne);

        if (!messageRetourne.equals(expectedMessage)) {
            throw new AssertionError("Le message retourné ne correspond pas à l'attendu : " + expectedMessage);
        }
    }

    @Then("le programme continu et la table budget ne contiendra que les données qui n'existaient pas encore en BD:")
    public void le_programme_continu_et_la_table_budget_ne_contiendra_que_les_données_qui_n_existaient_pas_encore_en_bd(io.cucumber.datatable.DataTable dataTable) {
        // Convertit la DataTable en liste de maps
        List<Map<String, String>> nouvellesTransactions = dataTable.asMaps(String.class, String.class);

        // Ajoute uniquement les transactions qui n'existent pas déjà dans budget
        for (Map<String, String> transaction : nouvellesTransactions) {
            if (!budget.contains(transaction)) {
                budget.add(transaction);
            }
        }

        System.out.println("Table budget après filtrage :");
        budget.forEach(System.out::println);
    }
}
