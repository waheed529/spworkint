package com.intellect.fna.repositories;

import java.util.List;
import java.util.prefs.Preferences;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository
public class RiskProfileDAO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PersistenceContext(unitName="fna")
	EntityManager em;
	
	@SuppressWarnings("unchecked")
	public JSONArray getRiskProfileQuestionaire(JSONObject riskProfileInputData) {
		logger.info("Entering getRiskProfileQuestionaire method in RiskProfileDAO");
		List<Object[]> lst=null; 
		JSONArray jsonFinalArray=new JSONArray();
		String baseNo=riskProfileInputData.getString("baseNo");
		if (readPreference("getRiskProfileQuestions_baseNo").equalsIgnoreCase(baseNo)) {

			if (!readPreference("getRiskProfileQuestions").equalsIgnoreCase("default")) {

				jsonFinalArray = JSONArray.fromObject(readPreference("getRiskProfileQuestions"));
				return jsonFinalArray;
			}
		} else {

			logger.debug("inserting into cache....");
			savePreference("getRiskProfileQuestions_baseNo", baseNo);
		}

		try {
			StringBuilder queryString = new StringBuilder(
					"WITH QUESTION_INFO AS ( SELECT DISTINCT QUESTION_NO,QUESTION_DESC,B.QUESTIONNAIRE_CODE,B.VERSION,(SELECT ANSWER_NO FROM MST_FNA_CLIENT_ANSWER ");
			queryString.append(
					"WHERE QUESTION_NO=A.QUESTION_NO AND FNA_ID=(SELECT FNA_ID FROM IWM_CS_CUSTOMER_MST WHERE CUST_NO=(SELECT CIF_NO FROM IWM_CS_BASE_MST WHERE CUST_NO=:cust_no)) ");
			queryString.append(
					"AND MST_FNA_CLIENT_ANSWER.QUESTIONNAIRE_CODE=A.QUESTIONNAIRE_CODE) AS PREVIOUSSELECTED FROM MST_FNA_QUESTIONAIRE A,MST_FNA_QUESTIONNAIRE_MASTER B ");
			queryString.append(
					"WHERE A.BR_CD=B.BR_CD AND B.QUESTIONNAIRE_CODE = A.QUESTIONNAIRE_CODE AND B.CUSTOMER_CATEGORY=(SELECT CATEGORY FROM IWM_CS_CUSTOMER_MST WHERE CUST_NO= ");
			queryString.append(
					"(SELECT CIF_NO FROM IWM_CS_BASE_MST WHERE CUST_NO=:cust_no))AND B.VERSION=(SELECT MAX(CAST(VERSION AS INT)) FROM MST_FNA_QUESTIONAIRE " );
			queryString.append("WHERE QUESTIONNAIRE_CODE=A.QUESTIONNAIRE_CODE) ORDER BY 1)");
			queryString.append(
					"SELECT A.QUESTION_NO,Q.QUESTION_DESC,A.QUESTIONNAIRE_CODE,Q.PREVIOUSSELECTED,A.ANSWER_NO,A.ANSWER_DESC,A.WEIGHTAGE,(SELECT PAR_DESC FROM CA840PB WHERE KEY_1 = 'RISK_PROFILE' ");
			queryString.append("AND BR_CD   =:brCde AND KEY_2 ='P' ||A.ANSWER_NO) LABEL ");
			queryString.append(
					"FROM MST_FNA_ANSWER_OPTIONS A ,QUESTION_INFO Q WHERE A.QUESTION_NO=Q.QUESTION_NO AND A.QUESTIONNAIRE_CODE=Q.QUESTIONNAIRE_CODE ");
			queryString.append("AND A.VERSION_NO =Q.VERSION ORDER BY A.QUESTION_NO,A.ANSWER_NO ");
			Query query = em.createNativeQuery(queryString.toString());
			query.setParameter("cust_no", riskProfileInputData.getString("cust_no"));
			query.setParameter("brCde", riskProfileInputData.getString("brCde"));
			lst = (List<Object[]>) query.getResultList();
			for (Object[] obj : lst) {
				boolean isNew=false;
				if (!jsonFinalArray.isEmpty()) {
					JSONObject jsonObject = jsonFinalArray.getJSONObject(jsonFinalArray.size() - 1);
					if (jsonObject.get("question_no").equals(obj[0].toString())) {
						JSONArray ansArray = jsonObject.getJSONArray("answer_options");
						JSONObject jsonObject1 = new JSONObject();
						jsonObject1.put("answer_no", obj[4].toString());
						jsonObject1.put("answer_desc", obj[5].toString());
						jsonObject1.put("weightage", obj[6].toString());
						jsonObject1.put("label", obj[7].toString());
						ansArray.add(jsonObject1);
					} else {
						isNew=true;
					}
				} else {
					isNew=true;
				}
				if(isNew)
				{
					JSONObject newQuestion = new JSONObject();
					JSONArray newAnsArray = new JSONArray();
					newQuestion.put("question_no", obj[0].toString());
					newQuestion.put("question_desc", obj[1].toString());
					newQuestion.put("questionnaire_code", obj[2].toString());
					newQuestion.put("previous_answer_selected", obj[3]==null?"":obj[3].toString());
					JSONObject ansJsonObject = new JSONObject();
					ansJsonObject.put("answer_no", obj[4].toString());
					ansJsonObject.put("answer_desc", obj[5].toString());
					ansJsonObject.put("weightage", obj[6].toString());
					ansJsonObject.put("label", obj[7].toString());
					newAnsArray.add(ansJsonObject);
					newQuestion.put("answer_options", newAnsArray);
					jsonFinalArray.add(newQuestion);
				}
			}
		}
         finally {
			em.close();
		}
		logger.info("Exiting getRiskProfileQuestionaire method in RiskProfileDAO");
		savePreference("getRiskProfileQuestions", jsonFinalArray.toString());
		return jsonFinalArray;
	}

	public JSONObject saveRiskProfile(String baseNo,String brCd,String[] answers,String[] weightage,String questionnaireCode,int length) {
		logger.info("Entering saveRiskProfile method in RiskProfileDAO");
		EntityTransaction entr = em.getTransaction();
		Object[] lst = null;
		JSONObject result = new JSONObject();
		int riskScoreTotal = 0;
		for (int i = 0; i < length; i++) {
			riskScoreTotal = riskScoreTotal + Integer.parseInt(weightage[i]);
		}
		try {
			entr.begin();

			String qry = "SELECT obj.risk_profile,obj.risk_profile_desc FROM MST_FNA_RISK_PROFILE obj where  obj.br_cd=:brCd and  obj.weightage_from<=:iWgtTotal and  obj.weightage_to>=:iWgtTotal and Obj.time_horizon_from=:timeFrom and Obj.time_horizon_to=:timeTo and Obj.QUESTIONNAIRE_CODE=(select category from iwm_Cs_customer_mst where cust_no=(select cif_no from iwm_Cs_base_mst where cust_no='"
					+ baseNo + "'))";
			Query query = em.createNativeQuery(qry);
			query.setParameter("brCd", brCd);
			query.setParameter("timeFrom", "0");
			query.setParameter("timeTo", "100");
			query.setParameter("iWgtTotal", Integer.toString(riskScoreTotal));
			lst = (Object[]) query.getResultList().get(0);

			String riskProfile = lst[0].toString();
			logger.debug("risk_profile.........." + riskProfile);
			String riskProfileDesc = lst[1].toString();
			logger.debug("risk_profile_desc.........." + riskProfileDesc);


			qry = "select to_char(bsns_dt,'DD-MON-YY'),to_char(add_months(bsns_dt, 12),'DD-MON-YY') from ca850mb where br_Cd='BR0001'";
			Query query1 = em.createNativeQuery(qry);
			lst = (Object[]) query1.getResultList().get(0);
			String date = lst[0].toString();
			String expdate = lst[0].toString();
			logger.debug("date............" + date);

			qry = "select fna_id,rm_id,cust_no,First_name||' '||Last_name from iwm_Cs_customer_mst where cust_no=(select cif_no from iwm_Cs_base_mst where cust_no='"
					+ baseNo + "')";
			query1 = em.createNativeQuery(qry);
			lst = (Object[]) query1.getResultList().get(0);
			String fnaId = lst[0].toString();
			String rmId = lst[1].toString();
			String cifNo = lst[2].toString();
			String name = lst[3].toString();

			logger.debug("fnaid............" + fnaId);
			qry = "delete FROM mst_fna_client_answer where fna_ID=(select fna_id from iwm_cs_customer_mst where cust_no=(select cif_no from iwm_cs_base_mst where cust_no='"
					+ baseNo + "'))";
			query1 = em.createNativeQuery(qry);
			int val = query1.executeUpdate();
			logger.debug("Deleted Records-------------->" + val);
			entr.commit();
			for (int i = 0; i < length; i++) {
				try {
					entr.begin();
					qry = "Insert into mst_fna_client_answer (PORTFOLIO_NO,QUESTION_NO,ANSWER_NO,BR_CD,LAST_UPD_DATE,VERSION,FNA_ID,QUESTIONNAIRE_CODE) values (null,"
							+ (i + 1) + "," + answers[i] + ",'" + brCd + "','" + date + "','0.0','" + fnaId + "','"
							+ questionnaireCode + "')";
					query1 = em.createNativeQuery(qry);
					val = query1.executeUpdate();
					logger.debug("Inserted Records-------------->" + val);
					entr.commit();
				} catch (Exception e) {
					logger.debug("Exception occured while fetching RiskProfileQuestionaire Details", e);
					 logger.error("Exception occured while fetching RiskProfileQuestionaire Details", e);
				}
			}

			qry = "SELECT B.asset_ret_per,A.asset_Per FROM LNK_FNA_RISK_PROF_ASSET_GRP A,MST_FNA_ASSET_GROUP B  WHERE A.br_Cd='"
					+ brCd + "' AND A.br_Cd=B.br_cd AND A.ASSET_GROUP_CD=B.asset_group_cd AND  A.RISK_PROFILE='"
					+ riskProfile + "' AND A.TIME_HORIZON_FROM='0' AND A.TIME_HORIZON_TO='100' ";
			query = em.createNativeQuery(qry);
			List<Object[]> rateList = (List<Object[]>) query.getResultList();
			int i = 0;
			double rateOfReturn = 0.0d;
			String results[][] = null;
			if (!rateList.isEmpty()) {
				results = new String[rateList.size()][2];
				for (Object obj : rateList) {
					Object[] arr = (Object[]) obj;
					results[i][0] = arr[0].toString();
					rateOfReturn = rateOfReturn
							+ (Double.parseDouble(arr[0].toString())) * (Double.parseDouble(arr[1].toString())) / 100;
					i++;
				}
			}

			try {
				entr.begin();

				qry = "delete FROM MST_FNA_PORTFOLIO where cif_no=(select cif_no from iwm_cs_base_mst where cust_no='"
						+ baseNo + "')";
				query1 = em.createNativeQuery(qry);
				val = query1.executeUpdate();
				logger.debug("Deleted Records-------------->" + val);
				entr.commit();
			} catch (Exception e) {
				logger.debug("Exception occured while fetching RiskProfileQuestionaire Details", e);
				 logger.error("Exception occured while fetching RiskProfileQuestionaire Details", e);
			}

			try {
				entr.begin();

				qry = "Insert into MST_FNA_PORTFOLIO (RISK_PROFILE,RATE_OF_RETURN,LAST_UPD_DATE,PORTFOLIO_STATUS,DECLARATION,INSTRUCTION1,INSTRUCTION2,RISK_SCORE,CIF_BASE_FLAG,QUESTIONNAIRE_CODE,MAKER_ID,MAKER_DATE,CUSTOMER_NAME,VERSION,BR_CD,FNA_ID,CIF_NO,BASE_NO,OD_PROSPECT_ID,EXPIRY_DATE,TIMEHORIZON,PORTFOLIO_NO,AUTH_ID,AUTH_DATE,INFL_RATE_INCOME,INFL_RATE_EXPENCE,CUST_ID,CLASSIFICATION,L2_RISK_SCORE,SUGSTD_RISK_PROFILE) values ('"
						+ riskProfile + "'," + rateOfReturn + ",'" + date + "','P','1',null,null," + riskScoreTotal
						+ ",'C','null," + questionnaireCode + "','" + rmId + "','" + date + "','" + name + "','0.0','"
						+ brCd + "','" + fnaId + "','" + cifNo + "',null,null,'" + expdate + "','0-100',null,'SYSTEM','"
						+ date + "',null,null,null,null," + riskScoreTotal + ",'" + riskProfile + "')";
				query1 = em.createNativeQuery(qry);
				val = query1.executeUpdate();
				logger.debug("Inserted Records-------------->" + val);
				entr.commit();
			} catch (Exception e) {
				logger.debug("Exception occured while fetching RiskProfileQuestionaire Details", e);
				 logger.error("Exception occured while fetching RiskProfileQuestionaire Details", e);
			}

			qry = "select LPAD(RISKPROFSEQ.NEXTVAL,7,0) from dual";
			query = em.createNativeQuery(qry);
			String risk_profile_id = (String) query.getResultList().get(0).toString();
			logger.debug("risk_profile_id..............." + risk_profile_id);

			qry = "SELECT ASSET_GROUP_CD,ASSET_GROUP_CD_DISPLAYLABEL,ASSET_PER FROM lnk_fna_risk_prof_asset_grp where risk_profile = '"
					+ riskProfile + "' and time_horizon_from = '0' and time_horizon_to = '100' and br_cd='" + brCd
					+ "'";
			query = em.createNativeQuery(qry);
			List<Object[]> lst1 = (List<Object[]>) query.getResultList();
			for (Object[] obj : lst1) {
				try {
					entr.begin();
					qry = "Insert into MST_FNA_CUSTOMER_ASSET_ALLOC (BR_CD,FNA_ID,RISK_PROFILE_ID,ASSET_GROUP_CD,ASSET_ALLOC_PER) values ('"
							+ brCd + "','" + fnaId + "','" + risk_profile_id + "','" + obj[0].toString() + "',"
							+ obj[2].toString() + ")";
					query1 = em.createNativeQuery(qry);
					val = query1.executeUpdate();
					logger.debug("Inserted Records-------------->" + val);
					entr.commit();
				} catch (Exception e) {
					logger.debug("Exception occured while fetching RiskProfileQuestionaire Details", e);
					 logger.error("Exception occured while fetching RiskProfileQuestionaire Details", e);
				}
			}

			result.put("result", "success");
			result.put("risk_profile_desc", riskProfileDesc);
			result.put("risk_profile", riskProfile);
			logger.debug("rateOfReturn................" + rateOfReturn);

		} catch (Exception e) {
			 result.put("result", "failure");
			 logger.debug("Exception occured while fetching RiskProfileQuestionaire Details", e);
			 logger.error("Exception occured while fetching RiskProfileQuestionaire Details", e);
		} finally {
			if (em != null)
				em.close();
		}
		logger.debug("RESULT+++" + result);
		logger.info("Exiting getRiskProfileQuestionaire method in RiskProfileDAO");
		return result;

	}

	public JSONArray getAllriskProfiles(String brCd) {
		logger.info("Entering getAllRiskProfiles method in RiskProfileDAO");
		String sql = "select PAR_DESC,value from ca840pb where key_1 = 'RISK_PROFILE' AND BR_CD = '" + brCd
				+ "' ORDER BY KEY_2";
		List<Object[]> lst = null;
		JSONArray details = new JSONArray();
		if(readPreference("getallriskprofiles_brCd").equalsIgnoreCase(brCd)){

			if(!readPreference("getallriskprofiles").equalsIgnoreCase("default")){
				
				details = JSONArray.fromObject(readPreference("getallriskprofiles"));
				return details;
			}
		}
		else
		{
			try {
				Query query = em.createNativeQuery(sql);
				lst = (List<Object[]>) query.getResultList();
				for (Object[] obj : lst) {
					JSONObject profiles = new JSONObject();
					JSONArray details1 = new JSONArray();

					profiles.put("risk_profile_desc", obj[0].toString());
					profiles.put("risk_profile", obj[1].toString());

					double rateOfReturn = 0.0d;
					double stdDev = 0.0d;
					String qry = "SELECT a.asset_group_cd_displaylabel,a.asset_group_cd,a.asset_per,b.asset_ret_per,b.asset_stdev FROM lnk_fna_risk_prof_asset_grp a,MST_FNA_ASSET_GROUP b WHERE time_horizon_from = '0' AND time_horizon_to   = '100' AND risk_profile = '"
							+ obj[1].toString() + "' and b.asset_group_cd=a.asset_group_cd order by risk_profile";
					query = em.createNativeQuery(qry);
					List<Object[]> lst1 = (List<Object[]>) query.getResultList();
					for (Object[] obj1 : lst1) {
						JSONObject profiles1 = new JSONObject();
						profiles1.put("asset_name", obj1[0].toString());
						profiles1.put("asset_cd", obj1[1].toString());
						profiles1.put("asset_per", obj1[2].toString());
						if (obj1[3] != null) {
							rateOfReturn = rateOfReturn + (Double.parseDouble(obj1[3].toString()))
									* (Double.parseDouble(obj1[2].toString())) / 100;
						} else {
							rateOfReturn = rateOfReturn
									+ (Double.parseDouble("0")) * (Double.parseDouble(obj1[2].toString())) / 100;
						}
						if (obj1[4] != null) {
							stdDev = stdDev + (Double.parseDouble(obj1[4].toString()))
									* (Double.parseDouble(obj1[2].toString())) / 100;
						} else {
							stdDev = stdDev + (Double.parseDouble("0")) * (Double.parseDouble(obj1[2].toString())) / 100;

						}
						details1.add(profiles1);
						profiles1 = null;
					}
					profiles.put("asset_alloc", details1);
					profiles.put("rate_of_return", rateOfReturn);
					profiles.put("stnd_dev", stdDev);

					details.add(profiles);
					details1 = null;

				}

			} catch (Exception e) {
				 logger.debug("Exception occured while fetching AllriskProfiles Details", e);
				 logger.error("Exception occured while fetching AllriskProfiles Details", e);
			} finally {
				if (em != null)
					em.close();
			}
			logger.debug("getallriskprofiles......" + details.toString());
			savePreference("getallriskprofiles", details.toString());
		}
		
		logger.info("Exiting getAllRiskProfiles method in RiskProfileDAO");
		return details;

	}
	public void savePreference(String key, String value) {
        Preferences prefs = Preferences.userNodeForPackage(RiskProfileDAO.class);

        prefs.put(key, value);
    }

    public String readPreference(String key) {
        Preferences prefs = Preferences.userNodeForPackage(RiskProfileDAO.class);

        return prefs.get(key, "default");
    }

}
