package com.agregateur.dimsoft.agregateur_production.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Component
public class FileTypeDetector {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Detect file type by scanning first lines:
     * - If a line contains "DATE" or "LIBEL" (libellé) and "DEBIT" or "CREDIT" => CA
     * - Else if the first cell of line 1 (or line 2) parses as date => LCL (skip 1)
     */
    public TypeFichier detect(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {

            String first = br.readLine();
            if (first == null) {
                return TypeFichier.UNKNOWN;
            }

            String firstUpper = first.toUpperCase(Locale.ROOT);

            // Heuristic: header line for CA contains DATE and LIBEL or DEBIT/CREDIT
            if (firstUpper.contains("DATE") && (firstUpper.contains("LIBEL") || firstUpper.contains("DEBIT") || firstUpper.contains("CREDIT"))) {
                return TypeFichier.CA;
            }

            // Maybe file has some preface lines; check the next few lines for a header or date
            String second = br.readLine();
            if (second != null) {
                String secondUpper = second.toUpperCase(Locale.ROOT);
                if (secondUpper.contains("DATE") && (secondUpper.contains("LIBEL") || secondUpper.contains("DEBIT") || secondUpper.contains("CREDIT"))) {
                    return TypeFichier.CA;
                }
            }

            // If first token of first line looks like a date -> LCL (but LCL we said skip first line)
            String firstToken = first.split(";")[0].trim();
            if (isDate(firstToken)) {
                return TypeFichier.LCL;
            }

            // Otherwise, try the second line first token
            if (second != null) {
                String secondToken = second.split(";")[0].trim();
                if (isDate(secondToken)) {
                    return TypeFichier.LCL;
                }
            }

            return TypeFichier.UNKNOWN;
        }
    }

    private boolean isDate(String token) {
        try {
            LocalDate.parse(token, DATE_FMT);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
