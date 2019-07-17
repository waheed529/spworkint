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
import com.intellect.fna.service.AlertsBO;
import com.intellect.fna.service.ProspectBO;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin
public class ProspectServiceRestController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public ProspectBO prospectBO;
	
	@Autowired
	public AlertsBO alertsBO;

	@RequestMapping(value="/getAllProspectsDetails",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String getAllProspectDetails(@RequestBody JSONObject prospectInputData)
			throws Exception {
		logger.info("Entering getAllProspectDetails method in ProspectServiceRestController");
		String value =prospectBO.getAllProspectDetails(prospectInputData,alertsBO).toString();
		logger.info("Exiting getAllProspectDetails method in ProspectServiceRestController");
		return CompressData.getCompressData(value);
	}
	
	@RequestMapping(value = "/addProspect", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON })
	public String saveProspectDetails(@RequestBody JSONObject prospectInputData) throws Exception {
		logger.info("Entering saveProspectDetails method in ProspectServiceRestController");
		/*
		 * String value =
		 * customerBO.getAllProspectDetails(prospectInputData,alertsBO).toString();
		 * return CompressData.getCompressData(value);
		 */
		logger.info("Exiting saveProspectDetails method in ProspectServiceRestController");
		return prospectBO.saveProspectDetails(prospectInputData).toString();
	}
}
