package com.intellect.fna.service;

import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class GoalPlanCalculator {

	public JSONObject calculateEducationPlanning(JSONObject inputObj) {
		double annualCostTerrEduc=inputObj.getDouble("annualCostTerrEdu");
		int factFutureValue=inputObj.getInt("factFutureValue");
		double totalannualTerrEducCost;
		double netROR=inputObj.getDouble("netROR");
		double existingEducationFunds=inputObj.getDouble("existingEducationFunds");
		double shortFallOrSurPlus;
		double totalEducationNeeds;
		totalannualTerrEducCost=annualCostTerrEduc*factFutureValue;
		totalEducationNeeds=totalannualTerrEducCost*netROR;
		shortFallOrSurPlus=totalEducationNeeds-existingEducationFunds;
		inputObj.put("shortFallOrSurPlus",shortFallOrSurPlus);
		return inputObj;
	}
	public JSONObject calculateWealthPlanning(JSONObject inputObj) throws Exception {
		double desirdLumpSumAmt=inputObj.getDouble("desirdLumpSumAmt");
		int noOfYearstoAccum=inputObj.getInt("noOfYearstoAccum");
		double netROR=inputObj.getDouble("netROR");
		double amountNeededLumpsum=desirdLumpSumAmt/netROR;
	    JSONArray lump = generateLumpsum(amountNeededLumpsum, noOfYearstoAccum, netROR);
	    double annuity = annuity(desirdLumpSumAmt, netROR, noOfYearstoAccum);
	    JSONArray annual = generateAnnualSum(annuity, noOfYearstoAccum, netROR);
		inputObj.put("amountNeededLumpsum",lump);
		inputObj.put("amountNeededAnnually",annual);
		return inputObj;
	}
	public JSONObject calculateRetireMent(JSONObject inputObj) throws Exception {
		double desirdAnnualRtdIncom=inputObj.getDouble("desirdAnnualRtdIncom");
		int noOfYearstoRetire=inputObj.getInt("noOfYearstoRetire");
		double netROR=inputObj.getDouble("netROR");
		double inflationRate=inputObj.getDouble("inflationRate");
		double annualRetmntIncome=desirdAnnualRtdIncom*inflationRate;
		int noOfYearstoProvide=inputObj.getInt("noOfYearstoProvide");
		double totalRetirementNeeds=annualRetmntIncome*netROR;
		double assetsLiquidated=inputObj.getDouble("assetsLiquidated");
		double shortFallOrSurPlus;
		shortFallOrSurPlus=totalRetirementNeeds-assetsLiquidated;
		inputObj.put("shortFallOrSurPlus",shortFallOrSurPlus);
		return inputObj;
	}
	public JSONObject calculateCriticalIllnessProtection(JSONObject inputObj) throws Exception {
		double annualIncomeNeeded=inputObj.getDouble("annualIncomeNeeded");
		int noOfYearstoProtect=inputObj.getInt("noOfYearstoProtect");
		double netROR=inputObj.getDouble("netROR");
		double inflationRate=inputObj.getDouble("inflationRate");
		double amountNeededtoTreat=inputObj.getDouble("amountNeededtoTreat");
		double captalSumNeeded=annualIncomeNeeded*netROR;
		double totalProtectionNeed=captalSumNeeded+amountNeededtoTreat;
		double existingProtection=inputObj.getDouble("existingProtection");;
		double shortFallOrSurPlus;
		shortFallOrSurPlus=totalProtectionNeed-existingProtection;
		inputObj.put("shortFallOrSurPlus",shortFallOrSurPlus);
		return inputObj;
	}
	public JSONObject calculateProtection(JSONObject inputObj) throws Exception {
		double annualIncomeTobeProtected=inputObj.getDouble("annualIncomeTobeProtected");
		int noOfYearstoProvide=inputObj.getInt("noOfYearstoProvide");
		double netROR=inputObj.getDouble("netROR");
		double inflationRate=inputObj.getDouble("inflationRate");
		double amountNeededtoTreat=inputObj.getDouble("amountNeededtoTreat");
		double totalFamIncomeTobeProtected=annualIncomeTobeProtected*netROR;
		double educationFunds=inputObj.getDouble("educationFunds");
		double outStandLiabilities=inputObj.getDouble("outstandLiabilities");
		double immediateExpences=inputObj.getDouble("immediateExpences");
		double othersExpences=inputObj.getDouble("othersExpences");
		double existingInsCoverage=inputObj.getDouble("existingInsCoverage");
		double totalIncomProtNeeds=totalFamIncomeTobeProtected+educationFunds+outStandLiabilities+immediateExpences+othersExpences;
		double assetsLiquidated=inputObj.getDouble("assetsLiquidated");
		double shortFallOrSurPlus=totalIncomProtNeeds-existingInsCoverage-assetsLiquidated;
		inputObj.put("shortFallOrSurPlus",shortFallOrSurPlus);
		return inputObj;
	}
	public JSONArray generateLumpsum(double amtlump, double year, double rtr) throws Exception{
		  JSONArray finalObj = new JSONArray();
		  double rtrnew = (1 + rtr/100);
		  for( int i=1; i<=year;i++){
				double amt = (amtlump) * (Math.pow(rtrnew, i));
				JSONObject temp = new JSONObject();
				temp.put("value", amt+"");
				finalObj.add(temp);
		  }
		  return finalObj;
	  }

	public JSONArray generateAnnualSum(double amtAnnual, double year, double rtr) throws Exception {

		JSONArray finalObj = new JSONArray();
		double rtrnew = (1 + rtr / 100);
		double growthrtr = 1.06;
		double last = 0;
		for (int i = 1; i <= year; i++) {
			double amt1 = (last) * (rtrnew);
			double amt2 = amtAnnual * (Math.pow(growthrtr, i - 1));
			last = amt1 + amt2;
			JSONObject temp = new JSONObject();
			temp.put("value", last + "");
			finalObj.add(temp);
		}
		return finalObj;
	}

	public double annuity(double goalYear, double portfolioGR, double years) {
		return (goalYear * (portfolioGR - 6) / 100)
				/ (Math.pow(1 + (portfolioGR / 100), years) - Math.pow(1.06, years));
	}
}
