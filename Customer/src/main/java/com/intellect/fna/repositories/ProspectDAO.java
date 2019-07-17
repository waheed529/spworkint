package com.intellect.fna.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.intellect.fna.service.AlertsBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository
public class ProspectDAO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PersistenceContext(unitName="fna")
	EntityManager em;
	
	@SuppressWarnings("unchecked")
	public JSONArray getAllProspectDetails(
			JSONObject prospectInputData, AlertsBO alertsBO) throws Exception {
		logger.info("Entering getAllProspectDetails method in ProspectDAO");
		JSONArray prospectOutData = null;
		String rmId = "";
		String brCd = "";
		List<Object[]> prospectList = null;
		try {
			if (prospectInputData != null) {
				rmId = prospectInputData.getString("rmId");
				brCd = prospectInputData.getString("brCd");
			}

			prospectOutData = new net.sf.json.JSONArray();
			StringBuilder queryString = new StringBuilder();
			queryString
					.append(" SELECT DISTINCT CASE WHEN c.category='01' THEN initcap(c.first_name || ' ' || NVL(c.middle_name,'') || ' ' || c.last_name) ELSE initcap(c.company_name) END prospect_name, ");
			queryString.append(" c.cust_no prospect_no, ");
			queryString.append(" c.category od_prospect_category,");
			queryString.append(" ca.par_desc prospect_status,");
			queryString
					.append(" (SELECT par_desc FROM ca840pb WHERE key_1 LIKE 'TRV_STATUS' AND br_cd =:brCd AND key_2 = c.customer_status ) customer_type, ");
			queryString
					.append(" NVL(c.risk_profile_desc, 'Not Profiled') risk_profile,");
			queryString.append(" u.user_name,");
			queryString.append(" u.user_id, ");
			queryString.append(" (SELECT value FROM iwm_cs_addr_cont_dtls_mst e WHERE flag='Contact' AND TYPE LIKE '%Email%' AND e.cust_no=c.cust_no AND rownum  <=1) Email, ");
			queryString.append(" (SELECT value FROM iwm_cs_addr_cont_dtls_mst e WHERE flag='Contact' AND TYPE LIKE '%Mobile%' AND e.cust_no=c.cust_no AND rownum  <=1 ) Mobile ");
			queryString
					.append(" FROM iwm_cs_customer_mst c, iwm_cs_bank_user_mst u, ca840pb ca ");
			queryString
					.append(" WHERE c.rm_id =:rmId AND c.rm_id = u.user_id AND c.br_cd = u.branch_code AND c.br_cd =:brCd AND ca.key_1= 'CUST_CATEGORY'");
			queryString
					.append(" AND ca.VALUE = c.category AND ca.mod_cd    = 'LP' AND c.record_type='PROSPECT' AND ca.lang_cd   = 'EN'");

			Query prospectQuery = em.createNativeQuery(queryString.toString());
			prospectQuery.setParameter("rmId", rmId);
			prospectQuery.setParameter("brCd", brCd);
			prospectList = prospectQuery.getResultList();
			for (Object[] obj : prospectList) {
				net.sf.json.JSONObject jobj = new net.sf.json.JSONObject();
				jobj.put("prospectName", obj[0].toString());
				jobj.put("prospectNo", obj[1].toString());
				jobj.put("prospectCategory", obj[2].toString());
				jobj.put("prospectStatus", obj[3].toString());
				jobj.put("rmName", obj[6].toString());
				JSONObject alertInput = new JSONObject();
				alertInput.put("rmId", obj[7].toString());
				alertInput.put("custNo", obj[1].toString());
				alertInput.put("custType", "PROSPECT");


				String alCnt = "0";
				JSONArray jarray =alertsBO
						.getAlertsCount(alertInput);
				if (jarray != null && !jarray.isEmpty()
						&& jarray.getJSONObject(0) != null
						&& jarray.getJSONObject(0).size() > 0)
					alCnt = jarray.getJSONObject(0).getString("ALERTCOUNT");
				jobj.put("alertCount", alCnt);
				if(obj[8]!=null)
					jobj.put("Email", obj[8].toString());
				if(obj[9]!=null)
					jobj.put("Mobile", obj[9].toString());
				
				prospectOutData.add(jobj);
			}
		} catch (Exception e) {
			 logger.debug("Exception occured while fetching Prospect Details", e);
			 logger.error("Exception occured while fetching Prospect Details", e);
		}
		logger.info("Exiting getAllProspectDetails method in ProspectDAO");
		return prospectOutData;
	}

	public JSONObject saveProspectDetails(JSONObject prospectInputData) {
		logger.info("Entering saveProspectDetails method in ProspectDAO");
		return prospectInputData;
	}
}
