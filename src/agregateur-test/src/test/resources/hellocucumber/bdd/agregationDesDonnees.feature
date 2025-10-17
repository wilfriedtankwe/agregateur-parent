Feature: agregation des données dans la table budget
  l'utilisateur veut pouvoir centraliser l'ensemble de ses transactions dans une table budget en BD
  pour ce faire il doit acceder à 4 fichiers dont un de type CA et 3 autres de type LCL et copier ligne
  par ligne les données qui s'y trouves en respectant les correspondances de champs.

  Background:
    Given les fichiers suivants sont disponibles dans le répertoire d'entrée
      | fichier                                                                                              |
      | src/test/resources/hellocucumber/fichier/CA20250820_115728.csv                                |
      | src/test/resources/hellocucumber/fichier/T_cpte_00737_691594V_du_22-05-2025_au_19-08-2025.csv |
      | src/test/resources/hellocucumber/fichier/T_cpte_01047_058113K_du_22-05-2025_au_19-08-2025.csv |
      | src/test/resources/hellocucumber/fichier/T_cpte_01049_911662L_du_22-05-2025_au_19-08-2025.csv |
    And  Données disponibles dans la base de Données
      |id |comment           |title                    |type |bank_id|
      | 1 |cpt courent       |CCHQ (X1797)             |depot| 2     |
      | 2 |cpt courent       |Compte de dépôts (X1594V)|depot| 3     |
      | 3 |cpt courent femme |compte de depot (X8113K) |depot| 4     |
      | 4 |cpt pro me lcl    |Compte Courant (x1662L)  |depot| 5     |




  Scenario Outline: Ouverture d’un fichier selon son état
    Given Un fichier avec le chemin "<chemin>" et donc l'etat est "<etat>"
    When Je tente d'ouvrir le fichier
    Then Le système doit afficher "<message>"

    Examples:
      | chemin                                                                                               | etat                 | message                                                       |
      | src/test/resources/hellocucumber/fichier/CA20250820_115728.csv                                        | existant             | fichier ouvert avec succès                                    |
      | src/test/resources/fichier/!#@!@LCL.csv                                                              | manquant             | une erreur "fichier introuvable" doit être remontée           |
      | !@!*&?*?invalid.csv                                                                                  | chemin invalide      | une erreur "chemin invalide" doit être remontée               |
      | src/test/resources/hellocucumber/fichier/T_cpte_00737_691594V_du_22-05-2025_au_19-08-2025.csv         | existant sans droits | une erreur "permission de lecture refusée" doit être remontée |
      | src/test/resources/hellocucumber/fichier/LCL2.csv                                                     | corrompu             | une erreur "fichier illisible" doit être remontée             |


  Scenario Outline: vérification de la structure d'un fichier
    Given que le nom du fichier "<fichier>" commence par CA
    When je me rend a la ligne onze du fichier "<fichier>"
    Then je dois retrouvé les champs nommés Date;Libellé;Débit euros;Crédit euros;

    Examples:
      | fichier                                                         | Date       | Libellé |Débit euros | Crédit euros |
      | src/test/resources/hellocucumber/fichier/CA20250820_115728.csv  | 12/05/2025 | vente    |          24 |          -24 |


  Scenario Outline: vérification de la structure d'un fichier
    Given que le nom du fichier "<fichier>" commence par T_cpte
    When je me place a la deuxieme ligne et je compte le nombre de colonne du fichier "<fichier>"
    Then je dois obtenir huit colonnes

    Examples:
      | fichier                                                                                              | date       | montant | mode de paiement |  | libellé        |  | zero | divers |
      | src/test/resources/hellocucumber/fichier/T_cpte_00737_691594V_du_22-05-2025_au_19-08-2025.csv | 12/08/2025 |    1250 | cheque           |  | Google playApp |  |    0 | divers |

  Scenario Outline: vérification de la structure d'un fichier quelconque
    Given que le nom du fichier ne commence ni par CA ni par T_cpte
    Then renvoyer un message "<message>" d'érreur

    Examples:
      | fichier                                            | message                         |
      | src/test/resources/Feature/TestFolder/misssing.csv | ce fichier ne peut etre importé |



  Scenario Outline: Récupération du numéro de compte
    Given Le fichier "<fichier>" se trouve dans le répertoire d'entrée
    When le système ouvre le fichier "<fichier>" valide contenant le numéro de compte "<numero>"
    Then le système lit le numéro de compte "<numero>"
    And l'extrait du fichier

    Examples:
      | fichier                                                                                               | numero        |
      | src/test/resources/hellocucumber/fichier/CA20250820_115728.csv                                 | 04109661797   |
      | src/test/resources/hellocucumber/fichier/T_cpte_00737_691594V_du_22-05-2025_au_19-08-2025.csv  | 01047 058113K |
      | src/test/resources/hellocucumber/fichier/T_cpte_01047_058113K_du_22-05-2025_au_19-08-2025.csv   | 01049 911662L |
      | src/test/resources/hellocucumber/fichier/T_cpte_01049_911662L_du_22-05-2025_au_19-08-2025.csv  | 00737 691594V |

  Scenario Outline: Récupération du numéro de compte avec échec
    Given Le fichier "<fichier>" se trouve dans le répertoire d'entrée
    When le système ouvre le fichier "<fichier>" et rencontre la situation "<situation>"
    Then une erreur "<message>" est levée

    Examples:
      | fichier                                                   | situation                  | message                    |
      | src/test/resources/hellocucumber/fichier/CA.csv    | nom de fichier sans numéro | nom de fichier invalide    |
      | src/test/resources/hellocucumber/fichier/CA.csv    | contenu du fichier vide    | ce fichier est vide        |
      | src/test/resources/hellocucumber/fichier/LCL.csv   | numéro de compte manquant  | aucun numero a ete trouve  |
      | src/test/resources/hellocucumber/fichier/LCL.csv   | contenu du fichier vide    | ce fichier LCL est vide    |
      | src/test/resources/hellocucumber/fichier/LCL1.csv  | numéro de compte manquant  | aucun numero a ete trouve  |
      | src/test/resources/hellocucumber/fichier/LCL1.csv  | contenu du fichier vide    | ce fichier LCL est vide    |
      | src/test/resources/hellocucumber/fichier/LCL2.csv  | numéro de compte manquant  | aucun numero a ete trouve  |
      | src/test/resources/hellocucumber/fichier/LCL2.csv  | contenu du fichier vide    | ce fichier LCL est vide    |


  Scenario Outline: Récupération de l'id de la banque avec numéro de compte non existant
    Given un numero de compte "<numeroCompte>"
    When  Je le formate en une  "<valeurFormatée>" selon les regles:
    And je recherche valeur formatée dans la colonne "title" de la base
    Then je retourne un message d'erreur  "Numero de compte pas pris en charge" si elle ne s'y trouve pas


    Examples:
      | numeroCompte  | valeurFormatée |
      | 04109661797   | X61797         |
      | 00737691594V  | X1594V         |
      | 01049911662L  | X1662L         |
      | 0107058113K   | X8113K         |

  Scenario Outline: Récupération de l'id de la banque avec numéro de compte existant
    Given un numero de compte "<numeroCompte>"
    When Je le formate en une  "<valeurFormatée>" selon les regles:
    And je recherche valeur formatée dans la colonne "title" de la base
    Then je retourne la valeur "<bank_id>" correspondante si elle se trouve dans la base

    Examples:
      | numeroCompte   | valeurFormatée |
      | 00737691594V   | X1594V         |
      | 01049911662L   | X1662L         |
      | 0107058113K    | X8113K         |
      | 04109661797    | X61797         |



  Scenario: agregation des données d'une transaction issue d'un fichier conforme et qui n'existe pas en base de données
    Given  le repertoire "src/test/resources/hellocucumber/fichier" auquel j'ai accès
    And dont la structure a été correctement identifiée et les numéros de "<compte>"  et l'id  "<bank>" de la banque ont été correctement recupérés
    When je lance l'agregation des données des transactions du fichier "fichier" dans la table budget
    Then la table budget contiendra les données de la nouvelle transaction
      | id | dateOperation | libelle                                | categorie                                 | montant | notes | chequeNumero | compte | bank |
      | 1  | 12/08/2025    | CHEQUE EMIS 8186609                    | CHEQUE EMIS 8186609                       | -150    |       |              | 1      | 2    |
      | 2  | 04/08/2025    | REGLEMENT ASSU. CNP PRET HABITAT 08/25 | REGLEMENT ASSU. CNP PRET HABITAT 08/25    | 59,62   |       |              | 1      | 2    |
      | 3  | 04/08/2025    | COTISATION Offre Compte à composer     | COTISATION Offre Compte à composer        | -4,68   |       |              | 1      | 2    |
    And le programme lui retournera un message de confirmation de copie de la forme "la copie s'est parfaitement deroulée"

  Scenario: agregation  annulée des données d'une transaction redondante issue d'un fichier conforme
    Given  une transaction redondante d'un fichier "fichier" du repertoire au cours d'un processus d'agregation de données
    When le programme me retourne un message de la forme "veux-tu annulée l'enregistrment en cours remarque aucune données ne sera ajouté en BD"
    And je valide l'annulation de la transaction
    Then la copie s'arrête
    And la table budget ne contient pas de nouvelle valeur:
      | id | dateOperation | libelle                            | categorie                          | montant | notes | chequeNumero | compte | bank |
      | 1  | 12/08/2025    | CHEQUE EMIS 8186609                | CHEQUE EMIS 8186609                | -150    |       |              | 1      | 2    |
      | 2  | 04/08/2025    | COTISATION Offre Compte à composer | COTISATION Offre Compte à composer | -4,68   |       |              | 1      | 2    |

  Scenario: agregation  des données d'une transaction déjà existante en BD issue d'un fichier conforme avec redonce
    Given  une transaction redondante d'un fichier "fichier" du repertoire au cours d'un processus d'agregation de données
    And un message de la forme "transaction déjà existante, veux-tu quand même enregistrer les données?"
    When je decide de continuer
    Then le programme continu et la table budget contiendra une fois de plus les données de la nouvelle transaction
      | id | dateOperation | libelle                            | categorie                         | montant | notes | chequeNumero | compte | bank |
      | 1  | 12/08/2025    | CHEQUE EMIS 8186609                | CHEQUE EMIS 8186609               | -150    |       |              | 1      | 2    |
      | 2  | 04/08/2025    | COTISATION Offre Compte à composer | COTISATION Offre Compte à composer| -4,68   |       |              | 1      | 2    |
      | 3  | 12/08/2025    | CHEQUE EMIS 8186609                | CHEQUE EMIS 8186609               | -150    |       |              | 1      | 2    |
    And le programme lui retournera un message de la forme "copie des données redondantes terminée avec succès"

  Scenario: agregation des données d'une transaction déjà existante en BD issue d'un fichier conforme sans rédonce
    Given  une transaction redondante au cours d'un processus d'agregation de données
    And un message de la forme " transaction déjà existante, veux-tu poursuivre l'agregation sans enregistrer ses données?"
    When je decide de continuer
    Then le programme continu et la table budget ne contiendra que les données qui n'existaient pas encore en BD:
      | id | dateOperation | libelle                            | categorie                          | montant | notes | chequeNumero | compte | bank |
      | 1  | 12/08/2025    | CHEQUE EMIS 8186609                | CHEQUE EMIS 8186609                | -150    |       |              | 1      | 2    |
      | 2  | 04/08/2025    | COTISATION Offre Compte à composer | COTISATION Offre Compte à composer | -4,68   |       |              | 1      | 2    |
      | 3  | 01/09/2025    | VIREMENT SALAIRE                   | REVENU SALAIRE                     | +300000 |       |              | 1      | 2    |
    And le programme lui retournera un message de la forme "copie des données sans redondance terminée avec succès"


  Scenario Outline: Fermeture d’un fichier  selon son état
    Given Le fichier avec le chemin "<chemin>" donc est "<etat>"
    When Je tente de fermer le fichier donc l'etat est "<etat>"
    Then Le système doit afficher "<message>"

    Examples:
      | chemin                                                                                              | etat       | message                                            |
      | src/test/resources/hellocucumber/fichier/CA20250820_115728.csv                               | ouverts    | le fichier doit être fermé sans probleme           |
      | src/test/resources/hellocucumber/fichier/T_cpte_00737_691594V_du_22-05-2025_au_19-08-2025.csv| déjà fermé | une erreur "fichier déjà fermé" doit être remontée |

