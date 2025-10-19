import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import services.TestConfig;


@CucumberContextConfiguration
@SpringBootTest(classes = {
        com.agregateur.dimsoft.agregateur_production.AgregateurProductionApplication.class,
        services.TestConfig.class
})

public class CucumberSpringConfiguration {
        // vide, Spring boot fera l’initialisation
    }

