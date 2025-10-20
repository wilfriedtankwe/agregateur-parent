package hellocucumber;

import com.agregateur.dimsoft.agregateur_production.AgregateurProductionApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Remplacez 'VotreApplicationPrincipale.class' par le nom réel de votre classe
// annotée avec @SpringBootApplication (ex: AgregateurApplication.class)
@CucumberContextConfiguration
@SpringBootTest(classes = AgregateurProductionApplication.class)
@ActiveProfiles("test")
public class CucumberSpringContextConfiguration {
    // Cette classe est le point de rencontre entre Spring et Cucumber
}