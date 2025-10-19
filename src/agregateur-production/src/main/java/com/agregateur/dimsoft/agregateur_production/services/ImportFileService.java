package com.agregateur.dimsoft.agregateur_production.services;

import com.agregateur.dimsoft.agregateur_production.beans.Budget;

import java.util.List;

public interface ImportFileService {
 public  String agregate(List<Budget> budgets);
}
