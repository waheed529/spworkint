package com.intellect.fna.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intellect.fna.repositories.FinancialDAO;

@Service
public class FinancialBO {
	@Autowired
	public FinancialDAO financialDAO;

	public String getDashboardHeader(String cif) throws Exception {
		String result = "";
		if (cif != null && !cif.equals("")) {
			result = financialDAO.getDashboardHeader(cif).toString();
		}

		return result;
	}

	public String getDashboardGraph(String cif) throws Exception {
		String result = "";
		if (cif != null && !cif.equals("")) {
			result = financialDAO.getDashboardGraph(cif).toString();
		}

		return result;
	}

	public String getDashboardHolding(String cif) throws Exception {
		String result = "";
		if (cif != null && !cif.equals("")) {
			result = financialDAO.getDashboardHolding(cif).toString();
		}

		return result;
	}

	public String getDashboardTransactions(String cif) throws Exception {
		String result = "";
		if (cif != null && !cif.equals("")) {
			result = financialDAO.getDashboardTransactions(cif).toString();
		}

		return result;
	}

}
