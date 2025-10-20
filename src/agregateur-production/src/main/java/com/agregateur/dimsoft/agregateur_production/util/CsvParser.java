package com.agregateur.dimsoft.agregateur_production.util;

import com.agregateur.dimsoft.agregateur_production.beans.Budget;
import com.agregateur.dimsoft.agregateur_production.factory.BudgetFactoryCa;
import com.agregateur.dimsoft.agregateur_production.factory.BudgetFactoryLcl;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class CsvParser {
    private final FileTypeDetector detector;

    public CsvParser() {
        this.detector = new FileTypeDetector();
    }

    public List<Budget> parse(Path path) throws IOException {
        TypeFichier type = detector.detect(path);
        if (type == TypeFichier.CA) {
            return parseCa(path);
        } else if (type == TypeFichier.LCL) {
            return parseLcl(path);
        } else {
            throw new IllegalArgumentException("Impossible de détecter le type de fichier pour : " + path);
        }
    }

    private List<Budget> parseLcl(Path path) throws IOException {
        // LCL: skip first line, no header, fixed positions expected
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toFile()))
                .withSkipLines(1) // skip first line
                .build()) {

            List<Budget> budgets = new ArrayList<>();
            String[] cols;
            while ((cols = reader.readNext()) != null) {
                if (cols.length == 0) continue;
                // some lines are short; pass to factory which will handle defensively
                budgets.add(BudgetFactoryLcl.fromCsv(cols));
            }
            return budgets;
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Budget> parseCa(Path path) throws IOException {
        // CA: we must find the header line first (line that contains "Date" / "Libellé")
        int headerLineIndex = findHeaderLineIndex(path);
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path.toFile()))
                .withSkipLines(headerLineIndex)
                .build()) {

            List<Budget> budgets = new ArrayList<>();
            String[] header = reader.readNext(); // header columns
            if (header == null) return budgets;

            // Map header indices (we can be flexible)
            CsvHeaderMapping mapping = CsvHeaderMapping.fromHeader(header);

            String[] cols;
            while ((cols = reader.readNext()) != null) {
                budgets.add(BudgetFactoryCa.fromCsv(cols, mapping));
            }
            return budgets;
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private int findHeaderLineIndex(Path path) throws IOException {
        try (BufferedReader br = java.nio.file.Files.newBufferedReader(path)) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                String upper = line.toUpperCase();
                if (upper.contains("DATE") && (upper.contains("LIBEL") || upper.contains("DEBIT") || upper.contains("CREDIT"))) {
                    return index;
                }
                index++;
            }
            // default: assume header at 0 if not found
            return 0;
        }
    }
}
