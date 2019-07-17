package com.intellect.fna.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intellect.fna.repositories.CustomerDAO;
import com.wms.intellect.service.customer.CustomerDetails;
import com.wms.intellect.service.customer.CustomerSearch;
import com.wms.intellect.wrapper.InterfaceCallThruWrapperService;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@Service
public class CustomerBO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public CustomerDAO customerDAO;
	
	public JSONArray getallcustomerdetails(String rmId) throws JSONException {
		logger.info("Entering getallcustomerdetails method in CustomerBO");
		JSONArray contactDetArr = new JSONArray();
		try {
			contactDetArr = customerDAO.getallcustomerdetails(rmId);
		} catch (Exception e) {
			logger.error("Exception occured while fetching Customer Details", e);
			logger.debug("Exception occured while fetching Customer Details", e);
		}
		logger.info("Exiting getallcustomerdetails method in CustomerBO");
		return contactDetArr;
	}
	public JSONArray getIdType() throws JSONException {
		logger.info("Entering getIdType method in CustomerBO");
		JSONArray idTypeArr = new JSONArray();
		try {
			idTypeArr = customerDAO.getIdType();
		} catch (Exception e) {
			logger.error("Exception occured while fetching Customer Details", e);
			logger.debug("Exception occured while fetching Customer Details", e);
		}
		logger.info("Exiting getIdType method in CustomerBO");
		return idTypeArr;
	}

	public JSONArray getCustomersOnSearch(JSONObject customerSearchInputdata) {
		logger.info("Entering getCustomersOnSearch method in CustomerBO");
		JSONArray customersJSONArray;
		customersJSONArray=customerDAO.getCustomersOnSearch(customerSearchInputdata);
		if(customersJSONArray!=null&&!customersJSONArray.isEmpty())
		{		
			logger.debug("Customers Found in FNA");
		    return customersJSONArray;
		}
		else
		{
			logger.debug("Customers Not Found in FNA");
			logger.debug("Retrieving Customer details from Core Banking system");
			if(customersJSONArray!=null)
				customersJSONArray=new JSONArray();
			InterfaceCallThruWrapperService interfaceCallThruWrapperService=new InterfaceCallThruWrapperService();
			CustomerDetails custDetails=new CustomerDetails();
			custDetails.setCimb_GovIssueIdentType(customerSearchInputdata.getString("idType"));
			custDetails.setCimb_IdentSerialNum(customerSearchInputdata.getString("idNum"));
			custDetails.setCimb_issue_country(customerSearchInputdata.getString("countryCode"));
			try {
				custDetails=interfaceCallThruWrapperService.getCustomerSearch(custDetails);
				if(custDetails!=null&&custDetails.getCustomerSearch()!=null)
				{
					for(CustomerSearch customerSearch:custDetails.getCustomerSearch())
					{
						JSONObject customerObject=new JSONObject();
						custDetails.setCUST_NO(customerSearch.getCUST_NO());
						custDetails=interfaceCallThruWrapperService.getCustomerDetails(custDetails);
						customerObject.put("name",custDetails.getFIRST_NAME());
						customerObject.put("base_no",custDetails.getCUST_NO());
						customerObject.put("fna_id",custDetails.getCUST_NO());
						customerObject.put("ccy_cd",custDetails.getCimb_issue_country());
						customerObject.put("current_value", "0");
						if(custDetails.getID_BIRTHDATE()!=null&&custDetails.getID_BIRTHDATE().length()==8)
						{
							SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf.parse(custDetails.getID_BIRTHDATE()));  
							LocalDate start = LocalDate.of(Integer.parseInt(custDetails.getID_BIRTHDATE().substring(0, 4)), Integer.parseInt(custDetails.getID_BIRTHDATE().substring(4, 6)), Integer.parseInt(custDetails.getID_BIRTHDATE().substring(6, 8)));
							LocalDate end = LocalDate.now(); 
							long years = ChronoUnit.YEARS.between(start, end);
							customerObject.put("age", years);
						}
						else
						{
							customerObject.put("age", "");
						}
						customerObject.put("txn_date", "");
						customerObject.put("email",custDetails.getEMAIL());
						customerObject.put("mobile", custDetails.getMOBILE_NO());
						customerObject.put("dob", customerSearch.getID_BIRTHDATE());
						customerObject.put("cust_cat", customerSearch.getCUST_CATEGORY());
						customerObject.put("riskprofile", "N");
						customerObject.put("wealth", "N");
						customerObject.put("retirement", "N");
						customerObject.put("education", "N");
						customerObject.put("protection", "N");
						customerObject.put("brcd", custDetails.getBRANCH());
						customerObject.put("salutation", custDetails.getSALUTATION());
						customerObject.put("alertcnt", "0");
						customerObject.put("unrealizedgain", "0");
						customerObject.put("alerts", "[\"\"]");
						customersJSONArray.add(customerObject);
					}
				}
			} catch (Exception e) {
				logger.error("Exception occured while searching Customer Details", e);
				logger.debug("Exception occured while searching Customer Details", e);
			}
		}
		logger.info("Exiting getCustomersOnSearch method in CustomerBO");
		return customersJSONArray;
	}
	public JSONObject saveDependentDetails(JSONObject dependentInputObj) {
		return customerDAO.saveDependentDetails(dependentInputObj);
	}

}
