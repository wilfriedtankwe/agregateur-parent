package com.agregateur.dimsoft.agregateur_production.controller;

import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.services.impl.ImportFileServiceImpl;
import com.agregateur.dimsoft.agregateur_production.util.CsvParser;
import com.agregateur.dimsoft.agregateur_production.util.TypeFichier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.util.List;

@RestController
public class ImportFileController {

    private final CsvParser csvParser;
    private final ImportFileServiceImpl importFileService;

    public ImportFileController(CsvParser csvParser, ImportFileServiceImpl importFileService) {
        this.csvParser = csvParser;
        this.importFileService = importFileService;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> aggregateFile(@RequestParam("filePath") String filePath, TypeFichier typeFichier) {
        try {
            List<Budget> budgets = csvParser.parse(Paths.get(filePath));
            String result = importFileService.agregate(budgets);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur: " + e.getMessage());
        }
    }

}
