package services;

import com.agregateur.dimsoft.agregateur_production.util.CsvParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    public CsvParser csvParser() {
        return new CsvParser();
    }
}