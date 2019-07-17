package com.intellect.fna.rest.controllers;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.intellect.fna.CompressData;
import com.intellect.fna.service.AlertsBO;
import com.intellect.fna.service.CustomerBO;
import com.intellect.fna.service.FinancialBO;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin
public class CustomerServiceRestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public CustomerBO customerBO;
	
	@Autowired
	public AlertsBO alertsBO;
	
	@Autowired
	public FinancialBO financialBO;
	
	@Autowired
	Environment env;
	
	@RequestMapping(value="/healthCheck",method=RequestMethod.GET,produces= {MediaType.APPLICATION_JSON})
	public String getHealthCheck()
	{
		logger.debug("The Server is up and running");
		return "The Customer Service is running";
	}
	
	@RequestMapping(value="/getallcustomerdetails",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String getAllCustomerDetails(@RequestBody JSONObject inputObj) throws Exception {
		String rmId = inputObj.getString("rmId");
		 return customerBO.getallcustomerdetails(rmId).toString();
	}
	@RequestMapping(value="/getIdType",
			method=RequestMethod.POST,
			produces= {MediaType.APPLICATION_JSON})
	public String getIdType() throws Exception {
		String jsonArr = customerBO.getIdType().toString();
	    return CompressData.getCompressData(jsonArr);
	}
	@RequestMapping(value="/getDetails",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String getCustomersOnSearch(@RequestBody JSONObject customerSearchInputData)
			throws Exception {
		return CompressData.getCompressData(customerBO.getCustomersOnSearch(customerSearchInputData).toString());
		
	}

	@RequestMapping(value = "/saveDependentDetails", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON })
	public String saveDependentDetails(@RequestBody JSONObject dependentInputObj) throws Exception {
		return customerBO.saveDependentDetails(dependentInputObj).toString();
	}

	@RequestMapping(value = "/getDashboardHeader", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON })
	public String getDashboardHeader(@RequestBody JSONObject inputObj) throws Exception {
		String cif = inputObj.getString("cif");
		return CompressData.getCompressData(financialBO.getDashboardHeader(cif));
	}

	@RequestMapping(value = "/getDashboardGraph", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON })
	public String getDashboardGraph(@RequestBody JSONObject inputObj) throws Exception {
		String cif = inputObj.getString("cif");
		return CompressData.getCompressData(financialBO.getDashboardGraph(cif));
	}

	@RequestMapping(value = "/getDashboardHolding", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON })
	public String getDashboardHolding(@RequestBody JSONObject inputObj) throws Exception {
		String cif = inputObj.getString("cif");
		return CompressData.getCompressData(financialBO.getDashboardHolding(cif));
	}

	@RequestMapping(value = "/getDashboardTransactions", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON })
	public String getDashboardTransactions(@RequestBody JSONObject inputObj) throws Exception {
		String cif = inputObj.getString("cif");
		return CompressData.getCompressData(financialBO.getDashboardTransactions(cif));
	}

}
