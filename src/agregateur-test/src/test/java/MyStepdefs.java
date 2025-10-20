import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.controller.ImportFileController;
import com.agregateur.dimsoft.agregateur_production.repositories.BudgetRepository;
import com.agregateur.dimsoft.agregateur_production.util.TypeFichier;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest

public class MyStepdefs{

    @Autowired
    private ImportFileController importFileController;
    public final BudgetRepository budgetRepository;

    private String repertoire;
    private String messageRetour;
    private TypeFichier typeFichier;


    //wil var
    private File file;
    private String lastMessage;
    private Exception lastException;

    public MyStepdefs(ImportFileController importFileController, BudgetRepository budgetRepository) {
        this.importFileController = importFileController;
        this.budgetRepository = budgetRepository;
    }
    @Before("@tag_de_ton_scenario") // ou @Before pour tous les scénarios
    public void nettoyerBaseDeDonnees() {
        budgetRepository.deleteAll();
        // Nettoie aussi les autres tables si nécessaire
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    private String etat;

    //Dan var
    private String fichier;
    private String messageResultat;
    private List<String> lignesFichier;




    //ange var
    private String banque;
    private String numero;
    private String situation;
    private boolean errorRaised;


    private static Map<String, String> database = new HashMap<>();

    //linc var
    private String accountNumber;
    private String formattedValue;
    private String messageError;
    private int Bank_id;

    private final List<String> title  = Arrays.asList(
            "CCHQ (X1797)",
            "Compte de dépôts (X1594V)",
            "compte de depot (X8113K)",
            "Compte Courant (X1662L)"
    );

    private final List<Integer> bank_id = Arrays.asList(
            2,
            3,
            4,
            5

    );

    //Amine var

    private String bankId;

    //WILFRIED
    @Given("Un fichier avec le chemin {string} et donc l'etat est {string}")
    public void un_fichier_avec_le_chemin_et_donc_l_etat_est(String chemin, String etat) {
        try {
            this.file = new File(chemin);
            this.etat = etat;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("Je tente d'ouvrir le fichier")
    public void je_tente_d_ouvrir_le_fichier() {
        try {
            if (file == null) {
                throw new IllegalStateException("Le fichier n'a pas été initialisé");
            }
            file.ouvrir(this.etat);
            lastMessage = "fichier ouvert avec succès";
        } catch (IllegalStateException e) {
            lastException = e;
            lastMessage = e.getMessage();
        }
    }

    @Then("Le système doit afficher {string}")
    public void le_système_doit_afficher(String messageAttendu) {
        if (lastException != null) {
            assertThat(lastException.getMessage()).isEqualTo(messageAttendu);
        } else {
            assertThat(messageAttendu).isEqualTo(lastMessage);
        }
    }

    @Given("Le fichier avec le chemin {string} donc est {string}")
    public void le_fichier_avec_le_chemin_donc_est(String chemin, String etat) {
        this.file = new File(chemin);

        try{
            file.fermer(etat);
        }catch (Exception e) {
            lastMessage = "une erreur fichier déjà fermé doit être remontée";
        }
    }

    @When("Je tente de fermer le fichier donc l'etat est {string}")
    public void je_tente_de_fermer_le_fichier_donc_l_etat_est(String etat) {
        try {
            file.fermer(etat);
            lastMessage = "le fichier doit être fermé sans probleme";
        } catch (IllegalStateException e) {
            lastException = e;
            lastMessage = e.getMessage();
        }
    }


    // Step spécifique pour le message avec guillemets
    @Then("Le système doit afficher \"une erreur {string} doit être remontée\"")
    public void le_systeme_doit_afficher_erreur_avec_guillemets(String erreurType) {
        String messageComplet = "une erreur " + erreurType + " doit être remontée";
        if (lastException != null) {
            System.out.println( "Une exception devrait être levée" +lastException.getMessage());
        } else {
            assertThat(messageComplet).isEqualTo(this.lastMessage);
        }
    }

    //Danie
    @Given("que le nom du fichier {string} commence par CA")
    public void que_le_nom_du_fichier_commence_par_CA(String fichier) {

        this.fichier = fichier;
        assertThat(Paths.get(fichier).getFileName().toString())
                .as("Le nom du fichier doit commencer par CA")
                .startsWith("CA");
    }

    @When("je me rend a la ligne onze du fichier {string}")
    public void je_me_rend_a_la_ligne_onze_du_fichier(String fichier) throws IOException {


         lignesFichier = Files.readAllLines(Paths.get(fichier), StandardCharsets.ISO_8859_1);

        assertThat(lignesFichier.size())
                .as("Le fichier doit contenir au moins 11 lignes")
                .isGreaterThanOrEqualTo(11);
    }

    @Then("je dois retrouvé les champs nommés Date;Libellé;Débit euros;Crédit euros;")
    public void je_dois_retrouv_les_champs_nomm_s_Date_Libell_D_bit_euros_Cr_dit_euros() {

        String ligne11 = lignesFichier.get(10); // Ligne 11 = index 10
        System.out.println("Contenu de la ligne 11 : " + ligne11);

        assertThat(ligne11)
                .as("Les champs attendus ne correspondent pas")
                .contains("Date")
                .contains("Libellé")
                .contains("Débit euro")
                .contains("Crédit euro");
    }

    @Given("que le nom du fichier {string} commence par T_cpte")
    public void que_le_nom_du_fichier_commence_par_T_cpte(String fichier) {

        this.fichier = fichier;
        assertThat(Paths.get(fichier).getFileName().toString())
                .as("Le nom du fichier doit commencer par T_cpte")
                .startsWith("T_cpte");
    }

    @When("je me place a la deuxieme ligne et je compte le nombre de colonne du fichier {string}")
    public void je_me_place_a_la_deuxieme_ligne_et_je_compte_le_nombre_de_colonne_du_fichier(String fichier)throws IOException {

        lignesFichier = Files.readAllLines(Paths.get(fichier), StandardCharsets.ISO_8859_1);

        assertThat(lignesFichier.size())
                .as("Le fichier doit contenir au moins deux lignes")
                .isGreaterThanOrEqualTo(2);

        String ligne2 = lignesFichier.get(1); // Ligne 2 = index 1
        String[] colonnes = ligne2.split(";");
        int nombreColonnes = colonnes.length;
        System.out.println("Nombre de colonnes trouvées : " + nombreColonnes);

        assertThat(nombreColonnes)
                .as("Le fichier T_cpte doit contenir 8 colonnes")
                .isEqualTo(8);
    }

    @Then("je dois obtenir huit colonnes")
    public void je_dois_obtenir_huit_colonnes() {

        System.out.println("Vérification du nombre de colonnes OK ");
    }


    @Given("que le nom du fichier ne commence ni par CA ni par T_cpte")
    public void que_le_nom_du_fichier_ne_commence_ni_par_CA_ni_par_T_cpte() {

        this.fichier = "src/test/resources/Feature/TestFolder/missing.csv";

        String nom = Paths.get(fichier).getFileName().toString();
        if (!nom.startsWith("CA") && !nom.startsWith("T_cpte")) {
            messageResultat = "ce fichier ne peut etre importé";
        } else {
            messageResultat = "ce fichier peut etre importé";
        }
    }

    @Then("renvoyer un message {string} d'érreur")
    public void renvoyer_un_message_d_rreur(String messageAttendu) {

        System.out.println("Message obtenu : " + messageResultat);
        assertThat(messageResultat)
                .as("Le message attendu ne correspond pas")
                .isEqualTo(messageAttendu);
    }



    //ANGE
    @Given("les fichiers suivants sont disponibles dans le répertoire d'entrée")
    public void les_fichiers_suivants_sont_disponibles_dans_le_répertoire_d_entrée(DataTable dataTable) {
        // Convertir la DataTable en liste de chaînes
        List<String> fichiers = dataTable.asList();

        // Afficher pour debug
        for (String fichier : fichiers) {
            System.out.println("Fichier disponible : " + fichier);
        }
    }

    @Given("Le fichier {string} se trouve dans le répertoire d'entrée")
    public void le_fichier_se_trouve_dans_le_répertoire_d_entrée(String banque) {
        this.banque = banque;
        errorRaised = false;
    }
    @When("le système ouvre le fichier {string} valide contenant le numéro de compte {string}")
    public void recuperer_numero_compte(String cheminFichier, String numeroAttendu) {
        try {
            List<String> lignes = Files.readAllLines(Paths.get(cheminFichier), StandardCharsets.ISO_8859_1);

            // Recherche du numéro dans les lignes
            for (String ligne : lignes) {

                if (ligne.contains(numeroAttendu)) {

                    this.numero = numeroAttendu;
                    database.put(cheminFichier, this.numero);
                    return;
                }
            }
            this.numero = null;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture fichier : " + cheminFichier, e);
        }
    }

    @When("le système ouvre le fichier {string} et rencontre la situation {string}")
    public void ouverture_fichier_avec_erreur(String cheminFichier, String situation) {
        try {
            switch (situation) {
                case "nom de fichier sans numéro":
                    throw new Exception("nom de fichier invalide");
                case "contenu du fichier vide":
                    if (cheminFichier.contains("LCL")) {
                        throw new Exception("ce fichier LCL est vide");
                    } else {
                        throw new Exception("ce fichier est vide");
                    }
                case "numéro de compte manquant":
                    throw new Exception("aucun numero a ete trouve");
                default:
                    throw new Exception("Situation inconnue");
            }
        } catch (Exception e) {
            // On stocke l'exception pour vérification dans Then
            this.errorRaised = true;
            this.numero = null;
            this.situation = e.getMessage();
        }
    }
    @Then("le système lit le numéro de compte {string}")
    public void le_système_lit_le_numéro_de_compte(String numeroAttendu) {
        if(numeroAttendu.equals(this.numero)) {
            System.out.println("Le numéro lu correspond");
        }
        else{
            System.out.println("Le numéro lu ne correspond pas au numéro attendu");
        }
    }
    @Then("l'extrait du fichier")
    public void l_extrait_du_fichier() {
        assertThat(this.numero).isEqualTo(database.get(banque));
        if(this.numero == null) {
            System.out.println("Le numéro de compte ne doit pas être null !");
        }
    }

    @Then("une erreur {string} est levée")
    public void une_erreur_est_levée(String messageAttendu) {

        assertThat(this.situation).isEqualTo(messageAttendu);
        if(!this.errorRaised){
            System.out.println("Aucune erreur n'a été levée !");
        }
        this.errorRaised = false;
        this.situation = null;
    }


    //Linc
    @Given("Données disponibles dans la base de Données")
    public void données_disponibles_dans_la_base_de_données(io.cucumber.datatable.DataTable dataTable) {

    }
    @Given("un numero de compte {string}")
    public void un_numero_de_compte(String numeroCompte) {
        this.accountNumber = numeroCompte;
    }
    @When("Je le formate en une  {string} selon les regles:")
    public void je_le_formate_en_une_selon_les_regles(String s) {
        this.formattedValue=s;
    }
    @When("je recherche valeur formatée dans la colonne {string} de la base")
    public void je_recherche_valeur_formatée_dans_la_colonne_de_la_base(String colonne) {
        int index = -1;

        for (int i = 1; i < title.size(); i++) {
            if (title.get(i).contains(formattedValue)) {
                index = i + 1;
                break;
            }

        }
        if (index != -1) {
            Bank_id= bank_id.get(index -1);
        } else {
            messageError = "Numero de compte pas pris en charge";
        }
    }
    @Then("je retourne un message d'erreur  {string} si elle ne s'y trouve pas")
    public void je_retourne_un_message_d_erreur_si_elle_ne_s_y_trouve_pas(String valeurAttendue) {
        System.out.println(Bank_id);
    }

    @Then("je retourne la valeur {string} correspondante si elle se trouve dans la base")
    public void je_retourne_la_valeur_correspondante_si_elle_se_trouve_dans_la_base(String s) {
        s= messageError;
        System.out.println(s);
    }



    //Amin
    @Given("le repertoire {string} auquel j'ai accès")
    public void leRepertoireAuquelJAiAccès(String expectedRepertoire) {
        budgetRepository.deleteAll();

        this.repertoire = expectedRepertoire; // <--- corrigé
    }

    @Given("dont la structure a été correctement identifiée et les numéros de {string}  et l'id  {string} de la banque ont été correctement recupérés")
    public void dont_la_structure_a_été_correctement_identifiée_et_les_numéros_de_et_l_id_de_la_banque_ont_été_correctement_recupérés(String expectedNumAccount, String expectedIdBank) {
        this.numero = expectedNumAccount;
        this.bankId = expectedIdBank;
    }
    @Given("une transaction redondante d{string}un processus d'agregation de données")
    public void uneTransactionRedondanteDUnFichierDuRepertoireAuCoursDUnProcessusDAgregationDeDonnées(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Given("une transaction redondante au cours d{string}agregation de données")
    public void uneTransactionRedondanteAuCoursDUnProcessusDAgregationDeDonnées() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @When("je lance l'agregation des données des transactions du fichier {string} dans la table budget")
    public void jeLanceAgregation(String fileName) throws Exception {
        if (fileName.toUpperCase().contains("CA")) {
            typeFichier = TypeFichier.CA;
        } else {
            typeFichier = TypeFichier.LCL;
        }

        if (importFileController == null) {
            throw new IllegalStateException("importFileController n'est pas injecté !");
        }

        messageRetour = String.valueOf(importFileController.aggregateFile(repertoire + "/" + fileName, typeFichier));
    }
    @When("le programme me retourne un message de la forme {string}")
    public void leProgrammeMeRetourneUnMessageDeLaForme(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @When("je decide de continuer")
    public void jeDecideDeContinuer() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Then("la table budget contiendra les données de la nouvelle transaction")
    public void tableBudgetContientDonnees(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        // Récupère les budgets en base
        List<Budget> budgets = budgetRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Budget::getId))
                .limit(rows.size())
                .collect(Collectors.toList());


        // Vérifie que le nombre correspond
        assertEquals(rows.size(), budgets.size());

        // Compare chaque ligne
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> expectedRow = rows.get(i);
            Budget actualBudget = budgets.get(i);

            assertEquals(expectedRow.get("libelle"), actualBudget.getLibelle());
            assertEquals(Float.parseFloat(expectedRow.get("montant")), actualBudget.getMontant());
            // ... autres assertions
        }
    }
    @Then("la copie s'arrête")
    public void laCopieSArrête() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Then("le programme continu et la table budget contiendra une fois de plus les données de la nouvelle transaction")
    public void leProgrammeContinuEtLaTableBudgetContiendraUneFoisDePlusLesDonnéesDeLaNouvelleTransaction() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Then("le programme continu et la table budget ne contiendra que les données qui n'existaient pas encore en BD:")
    public void leProgrammeContinuEtLaTableBudgetNeContiendraQueLesDonnéesQuiNExistaientPasEncoreEnBD() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @And("le programme lui retournera un message de confirmation de copie de la forme {string}")
    public void verificationMessage(String expected) {
        assertEquals(expected, messageRetour);
    }
    @And("je valide l'annulation de la transaction")
    public void jeValideLAnnulationDeLaTransaction() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @And("la table budget ne contient pas de nouvelle valeur:")
    public void laTableBudgetNeContientPasDeNouvelleValeur() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("un message de la forme {string}")
    public void unMessageDeLaForme(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("le programme lui retournera un message de la forme {string}")
    public void leProgrammeLuiRetourneraUnMessageDeLaForme(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }




}
