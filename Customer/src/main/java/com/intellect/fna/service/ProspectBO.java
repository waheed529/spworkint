package com.intellect.fna.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intellect.fna.repositories.ProspectDAO;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@Service
public class ProspectBO {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ProspectDAO prospectDAO;

	public JSONArray getAllProspectDetails(
			JSONObject prospectInputData, AlertsBO alertsBO) throws JSONException {
		logger.info("Entering getAllProspectDetails method in ProspectBO");
		JSONArray prospectOutData = new JSONArray();
		try {
			prospectOutData = prospectDAO.getAllProspectDetails(prospectInputData,alertsBO);
		} catch (Exception e) {
			logger.error("Exception occured while fetching Prospect Details", e);
			logger.debug("Exception occured while fetching Prospect Details", e);
		}
		logger.info("Entering getAllProspectDetails method in ProspectBO");
		return prospectOutData;
	}

	public JSONObject saveProspectDetails(JSONObject prospectInputData) {
		logger.info("Entering saveProspectDetails method in ProspectBO");
		try {
			prospectInputData = prospectDAO.saveProspectDetails(prospectInputData);
		} catch (Exception e) {
			logger.error("Exception occured while saving Prospect Details", e);
			logger.debug("Exception occured while saving Prospect  Details", e);
		}
		logger.info("Entering saveProspectDetails method in ProspectBO");
		return prospectInputData;
	}
}
