package com.agregateur.dimsoft.agregateur_production.controller;


import com.agregateur.dimsoft.agregateur_production.services.impl.ImportFileServiceImpl;
import com.agregateur.dimsoft.agregateur_production.services.impl.ParseurCAFile;
import com.agregateur.dimsoft.agregateur_production.services.impl.ParseurLLCFile;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/files")
public class ImportFileController {

    private final ImportFileServiceImpl importFileService;
    private final ParseurCAFile parseurCAFile;
    private final ParseurLLCFile parseurLLCFile;

    public ImportFileController(ImportFileServiceImpl importFileService, ParseurCAFile parseurCAFile, ParseurLLCFile parseurLLCFile) {
        this.importFileService = importFileService;
        this.parseurCAFile = parseurCAFile;
        this.parseurLLCFile = parseurLLCFile;
    }



    @GetMapping("/extract-account-number")
    public Map<String, ParseurCAFile.AccountResult> extractNumbers1() {
        return parseurCAFile.extractNumbersFromFiles();
    }

    @GetMapping("/extract-account-numberLCL")
    public Map<String, ImportFileServiceImpl.AccountResult> extractNumbers() {
        return parseurLLCFile.extractNumbersFromFilesLCL();
    }


}
