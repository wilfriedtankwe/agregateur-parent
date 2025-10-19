package com.agregateur.dimsoft.agregateur_production.services;

import com.agregateur.dimsoft.agregateur_production.services.impl.ImportFileServiceImpl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ImportFileService {
    Map<String, ImportFileServiceImpl.AccountResult> extractNumbersFromFiles() ;


}
