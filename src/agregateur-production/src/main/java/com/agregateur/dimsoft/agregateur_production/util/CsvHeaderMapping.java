package com.agregateur.dimsoft.agregateur_production.util;

import java.util.HashMap;
import java.util.Map;

public class CsvHeaderMapping {
    private final Map<String, Integer> indexByNormalizedName = new HashMap<>();

    public static CsvHeaderMapping fromHeader(String[] header) {
        CsvHeaderMapping m = new CsvHeaderMapping();
        for (int i = 0; i < header.length; i++) {
            String col = header[i] == null ? "" : header[i].trim().toUpperCase();
            m.indexByNormalizedName.put(col, i);
        }
        return m;
    }

    /**
     * find index by any header candidate (case-insensitive)
     */
    public int indexOf(String... candidates) {
        for (String cand : candidates) {
            String key = cand == null ? "" : cand.trim().toUpperCase();
            for (Map.Entry<String, Integer> e : indexByNormalizedName.entrySet()) {
                if (e.getKey().contains(key) || key.contains(e.getKey())) {
                    return e.getValue();
                }
            }
        }
        return -1;
    }
}
