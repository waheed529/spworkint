package com.intellect.fna.rest.controllers;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.intellect.fna.CompressData;
import com.intellect.fna.service.RiskProfileBO;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin
public class RiskProfileServiceRestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public RiskProfileBO riskProfileBO;
	
	@RequestMapping(value="/getRiskProfileQuestionaire",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String getRiskProfileQuestionaire(@RequestBody JSONObject prospectInputData)
			throws Exception {
		logger.info("Entering getRiskProfileQuestionaire method in RiskProfileServiceRestController");
		return CompressData.getCompressData(riskProfileBO.getRiskProfileQuestionaire(prospectInputData).toString());
	}
	@RequestMapping(value="/saveRiskProfile",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String saveRiskProfile(@RequestBody JSONObject riskProfileInputObj)
			throws Exception {
		logger.info("Entering saveRiskProfile method in RiskProfileServiceRestController");
		String baseProsCifNo = riskProfileInputObj.getString("baseProsCifNo");
		String brCd = riskProfileInputObj.getString("brCd");
		String questionnaireCode = riskProfileInputObj.getString("questionnaireCode");
        int noOfQuestions=riskProfileInputObj.getJSONArray("answers").size(); 
		String[] answersSelected = new String[noOfQuestions];
		String[] weightage = new String[noOfQuestions];
		for (int i = 0; i < noOfQuestions; i++) {
			answersSelected[i] = riskProfileInputObj.getJSONArray("answers").getJSONObject(i).getString("selected");

			weightage[i] = riskProfileInputObj.getJSONArray("answers").getJSONObject(i).getString("weightage");
		}
		String value = riskProfileBO.saveRiskProfile(baseProsCifNo, brCd, answersSelected, weightage,
				questionnaireCode, noOfQuestions);
		logger.info("Exiting saveRiskProfile method in RiskProfileServiceRestController");
		return CompressData.getCompressData(value);
	}
	@RequestMapping(value="/getallriskprofiles",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String getAllRiskProfiles(@RequestBody JSONObject inputObj) throws Exception {
		logger.info("Entering getAllRiskProfiles method in RiskProfileServiceRestController");
		String brCd = inputObj.getString("brCd");
		String value=riskProfileBO.getAllRiskProfiles(brCd);
		logger.info("Exiting getAllRiskProfiles method in RiskProfileServiceRestController");
		return CompressData.getCompressData(value);
		}
}
