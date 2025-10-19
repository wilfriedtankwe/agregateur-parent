package com.agregateur.dimsoft.agregateur_production.controller;


import com.agregateur.dimsoft.agregateur_production.services.impl.ImportFileServiceImpl;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/files")
public class ImportFileController {

    private final ImportFileServiceImpl importFileService;

    public ImportFileController(ImportFileServiceImpl importFileService) {
        this.importFileService = importFileService;
    }



    @GetMapping("/extract-account-number")
    public Map<String, ImportFileServiceImpl.AccountResult> extractNumbers() {
        return importFileService.extractNumbersFromFiles();
    }



}
