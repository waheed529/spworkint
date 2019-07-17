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
import com.intellect.fna.service.GoalPlanCalculator;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin
public class PlanCalculatorController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public GoalPlanCalculator goalPlanCalculator;
	
	@Autowired
	public AlertsBO alertsBO;
	
	@RequestMapping(value="/calculateEducation",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String calculateEducationPlanning(@RequestBody JSONObject inputObj) throws Exception {
		 return CompressData.getCompressData(goalPlanCalculator.calculateEducationPlanning(inputObj).toString());
	}
	@RequestMapping(value="/calculateRetirement",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String calculateRetirementPlanning(@RequestBody JSONObject inputObj) throws Exception {
		 return CompressData.getCompressData(goalPlanCalculator.calculateRetireMent(inputObj).toString());
	}
	@RequestMapping(value="/calculateWealth",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String calculateWealthPlanning(@RequestBody JSONObject inputObj) throws Exception {
		 return CompressData.getCompressData(goalPlanCalculator.calculateWealthPlanning(inputObj).toString());
	}
	@RequestMapping(value="/calculateProtection",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String calculateProtection(@RequestBody JSONObject inputObj) throws Exception {
		 return CompressData.getCompressData(goalPlanCalculator.calculateProtection(inputObj).toString());
	}
	@RequestMapping(value="/calculateCriticalIllnessProtection",
			method=RequestMethod.POST,
			consumes= {MediaType.APPLICATION_JSON},
			produces= {MediaType.APPLICATION_JSON})
	public String calculateCriticalIllnessProtection(@RequestBody JSONObject inputObj) throws Exception {
		 return CompressData.getCompressData(goalPlanCalculator.calculateCriticalIllnessProtection(inputObj).toString());
	}
}
