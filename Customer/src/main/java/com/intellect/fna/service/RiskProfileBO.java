package com.intellect.fna.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intellect.fna.repositories.RiskProfileDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class RiskProfileBO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	RiskProfileDAO riskProfileDAO;

	public JSONArray getRiskProfileQuestionaire(JSONObject riskProfileInputData) {
		logger.info("Entering getRiskProfileQuestionaire method in RiskProfileBO");
		return riskProfileDAO.getRiskProfileQuestionaire(riskProfileInputData);
	}

	public String saveRiskProfile(String baseProsCifNo, String brCd, String[] answersSelected, String[] weightage,
			String questionnaireCode, int size) {
		logger.info("Entering saveRiskProfile method in RiskProfileBO");
		return  riskProfileDAO.saveRiskProfile(baseProsCifNo,brCd,answersSelected,weightage,questionnaireCode,size).toString();
	}

	public String getAllRiskProfiles(String brCd) throws Exception{
		logger.info("Entering getAllRiskProfiles method in RiskProfileBO");
		JSONArray benchdet;
		benchdet=riskProfileDAO.getAllriskProfiles(brCd);
		logger.info("Exiting getAllRiskProfiles method in RiskProfileBO");
		return benchdet.toString();

	}

}
