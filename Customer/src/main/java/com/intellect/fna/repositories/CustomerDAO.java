package com.intellect.fna.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository
@SuppressWarnings("unchecked")
public class CustomerDAO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@PersistenceContext(unitName="fna")
	EntityManager em;
	
	public JSONArray getallcustomerdetails(String rmId) {
		List<Object[]> lst=null; 
		JSONArray finalArr= new JSONArray()	;
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT BASE_NAME,CIFNO,FNA_ID,CUST_ID,MOBILE,EMAIL,ALERTCNT,PREFERED,CURRENT_VALUE AUM,BASE_NAME_THAI FROM IWM_RMW_CUST_DTLS A WHERE RM_ID=:rmId ORDER BY BASE_NAME"); 
		try {

			Query query = em.createNativeQuery(queryString.toString());
			query.setParameter("rmId", rmId);
			lst = (List<Object[]>) query.getResultList();
			for (Object[] obj : lst) {
				JSONObject newCustomer = new JSONObject();
				newCustomer.put("basename", obj[0] == null ? "" : obj[0].toString());
				newCustomer.put("cif", obj[1] == null ? "" : obj[1].toString());
				newCustomer.put("fna_id", obj[2] == null ? "" : obj[2].toString());
				newCustomer.put("custId", obj[3] == null ? "" : obj[3].toString());
				newCustomer.put("mobile", obj[4] == null ? "" : obj[4].toString());
				newCustomer.put("email", obj[5] == null ? "" : obj[5].toString());
				newCustomer.put("alertcnt", obj[6] == null ? "" : obj[6].toString());
				newCustomer.put("preffered", obj[7] == null ? "" : obj[7].toString());
				newCustomer.put("aum", obj[8] == null ? "" : obj[8].toString());
				newCustomer.put("thaiName", obj[9] == null ? "" : obj[9].toString());
				finalArr.add(newCustomer);
			}
		} catch (Exception e) {
			logger.debug("Exception occured while fetching Customer Details", e);
			logger.error("Exception occured while fetching Customer Details", e);
		} finally {
			if (em != null)
				em.close();
		}
		logger.info("Exiting getCustomerdetails method in CustomerDAO");
		return finalArr;
		//return getCustomerdetails(rmId, true)	;
	}
	public JSONArray getCustomersOnSearch(JSONObject customerSearchInputData) {
		return  getCustomerdetails(customerSearchInputData.getString("rmId"), false);
	}
	
	public JSONArray getCustomerdetails(String rmId,boolean isRMMappedCustomers) {
		logger.info("Entering getCustomerdetails method in CustomerDAO");
		List<Object[]> lst=null; 
		JSONArray finalArr= new JSONArray()	;
		StringBuilder queryString = new StringBuilder();
		queryString.append("WITH ALERTDETAILS AS(SELECT DISTINCT OBJ.SUB_CAT_CODE ,OBJ.CUST_NO FROM IWM_RMW_ALERT_MST MASTOBJ,IWM_RMW_ALERT_DTL OBJ   ");
		queryString.append("WHERE OBJ.AR_TYPE           ='A' AND OBJ.SUB_CAT_CODE      = MASTOBJ.SUB_CAT_CODE AND OBJ.BR_CD             = MASTOBJ.BR_CD ");
		queryString.append("AND MASTOBJ.SUB_CAT_CODE IN('PORTFOLIO_REBALANCE','FALL_IN_AUM','DOC_ABT_EXP','RISK_PROFILE_DEVIATION','PENDING_ORDER') ");
		queryString.append("ORDER BY CUST_NO)");
		queryString.append("SELECT A.*,AD.SUB_CAT_CODE FROM (SELECT A.BASE_NAME BASE_NAME,  A.CUST_NO BASE_NO,(SELECT SUM( NVL(CURRENT_VALUE,0)) AUM ");
		queryString.append("FROM IWM_RMW_CUST_DTLS  WHERE BASE_NO=A.CUST_NO  AND RM_ID    =A.RM_ID   ) AS AUM, TRUNC(MONTHS_BETWEEN(SYSDATE,D.DATE_OF_BIRTH)/12) AGE,");
		queryString.append("(SELECT TO_CHAR(MAX(NVL(PURCHASED_ON,SOLD_ON)), 'DD-MON-YYYY HH:MI:SS') TXN_DATE  FROM TB_PA_TRANSACTIONS_MST D WHERE D.BASE_NO=BASE_NO)TXN_DATE,");
		queryString.append("(SELECT VALUE FROM IWM_CS_ADDR_CONT_DTLS_MST E WHERE FLAG='CONTACT' AND TYPE LIKE '%EMAIL%' AND E.CUST_NO=D.CUST_NO AND ROWNUM  <=1) EMAIL,");
		queryString.append("(SELECT VALUE FROM IWM_CS_ADDR_CONT_DTLS_MST E WHERE FLAG='CONTACT' AND TYPE LIKE '%MOBILE%' AND E.CUST_NO=D.CUST_NO AND ROWNUM  <=1) MOBILE,");
		queryString.append("D.FNA_ID FNAID, D.DATE_OF_BIRTH AS DOB,(SELECT PAR_DESC FROM CA840PB WHERE KEY_1='CUST_CATEGORY' AND VALUE  =D.CATEGORY ) CUST_CATEGORY,");
		queryString.append("(SELECT PAR_DESC FROM CA840PB WHERE KEY_1='RISKPROFILE' AND VALUE  =D.RISK_PROFILE AND BR_CD  ='BR0001') RISK_PROFILE, D.BR_CD, B.SHORT_DESC,D.SALUTATION,");
		queryString.append("(SELECT NVL(ALERTCNT,0) FROM IWM_RMW_CUST_DTLS WHERE BASE_NO=A.CUST_NO AND RM_ID    =A.RM_ID) AS ALERTCNT,");
		queryString.append("(SELECT F.UNREALIZEDGAIN FROM IWM_UNREALIZEDGAIN_VIEW F WHERE F.BASE_NO=A.CUST_NO) UNREALIZEDGAIN,");
		queryString.append("G.GOAL_SOURCE GOAL_SOURCE,D.ID_NAME,D.ID_VALUE  ");
		queryString.append("FROM IWM_CS_BASE_MST A ,IWM_CS_BASE_CIF_MAP_MST C,CA800MB B, IWM_CS_CUSTOMER_MST D ,MST_FNA_FIN_GOAL G WHERE C.PRIMARY_FLAG='Y' AND D.CUST_NO       = C.CIF_NO ");
		queryString.append("AND D.CUST_NO= A.CIF_NO AND A.CUST_NO= C.CUST_NO AND B.CCY_CD = A.REFERENCE_CCY AND B.BR_CD= A.BR_CD  AND G.FNA_ID= D.FNA_ID ");
		if(isRMMappedCustomers)
			queryString.append("AND A.RM_ID=:rmId) A LEFT OUTER JOIN ALERTDETAILS AD ON AD.CUST_NO=A.BASE_NO");
		else
			queryString.append("AND A.RM_ID<>:rmId) A LEFT OUTER JOIN ALERTDETAILS AD ON AD.CUST_NO=A.BASE_NO");
		try{
			
		    Query query =em.createNativeQuery(queryString.toString());
		    query.setParameter("rmId", rmId);
		    lst = (List<Object[]>)query.getResultList();
		    for(Object[] obj:lst){
				  boolean isNew=false;
					if (!finalArr.isEmpty()) {
						JSONObject jobj = finalArr.getJSONObject(finalArr.size() - 1);
					if (jobj.get("base_no").equals(obj[1].toString())) {
						if (obj[16] != null && "Wealth Planning".equals(obj[16].toString())) {
							jobj.put("wealth", "Y");
							continue;
						} else {
							jobj.put("wealth", "N");
						}
						if (obj[16] != null && "Retirement Planning".equals(obj[16].toString())) {
							jobj.put("retirement", "Y");
							continue;
						} else {
							jobj.put("retirement", "N");
						}
						if (obj[16] != null && "Education Planning".equals(obj[16].toString())) {
							jobj.put("education", "Y");
							continue;
						} else {
							jobj.put("education", "N");
						}
						if (obj[16] != null && "Protection".equals(obj[16].toString())) {
							jobj.put("protection", "Y");
							continue;
						} else {
							jobj.put("protection", "N");
						}
						} else {
							isNew=true;
						}
					} else {
						isNew=true;
					}
					if(isNew)
				{
					JSONObject newCustomer = new JSONObject();
					newCustomer.put("name", obj[0].toString());
					newCustomer.put("base_no", obj[1].toString());
					newCustomer.put("fna_id", obj[7].toString());
					newCustomer.put("ccy_cd", obj[12].toString());
					newCustomer.put("current_value", obj[2] == null ? "" : obj[2].toString());
					newCustomer.put("age", obj[3] == null ? "" : obj[3].toString());
					newCustomer.put("txn_date", obj[4].toString());
					newCustomer.put("email", obj[5] == null ? "" : obj[5].toString());
					newCustomer.put("mobile", obj[6] == null ? "" : obj[6].toString());
					newCustomer.put("dob", obj[8] == null ? "" : obj[8].toString());
					newCustomer.put("cust_cat", obj[9] == null ? "" : obj[9].toString());
					newCustomer.put("riskprofile", obj[10] == null ? "" : obj[10].toString());
					newCustomer.put("brcd", obj[11] == null ? "" : obj[11].toString());
					if (obj[16] != null && "Wealth Planning".equals(obj[16].toString()))
						newCustomer.put("wealth", "Y");
					else
						newCustomer.put("wealth", "N");
					if (obj[16] != null && "Education Planning".equals(obj[16].toString()))
						newCustomer.put("retirement", "Y");
					else
						newCustomer.put("retirement", "N");
					if (obj[16] != null && "Retirement Planning".equals(obj[16].toString()))
						newCustomer.put("education", "Y");
					else
						newCustomer.put("education", "N");
					if (obj[16] != null && "Protection".equals(obj[16].toString()))
						newCustomer.put("protection", "Y");
					else
						newCustomer.put("protection", "N");
					newCustomer.put("salutation", obj[13] == null ? "" : obj[13].toString());
					// Alert Count
					newCustomer.put("alertcnt", obj[14] == null ? "" : obj[14].toString());
					newCustomer.put("unrealizedgain", obj[15] == null ? "" : obj[15].toString());
					newCustomer.put("alerts", obj[19] == null ? "" : obj[19].toString());
					newCustomer.put("idType", obj[17] == null ? "" : obj[17].toString());
					newCustomer.put("idValue", obj[18] == null ? "" : obj[18].toString());
					finalArr.add(newCustomer);
				}
		    }
		 }catch (Exception e) {
			 logger.debug("Exception occured while fetching Customer Details", e);
			 logger.error("Exception occured while fetching Customer Details", e);
			} finally {
				if(em!=null)
					em.close();
			}
		logger.info("Exiting getCustomerdetails method in CustomerDAO");
		return finalArr;
	}
	public JSONArray getIdType() {
		logger.info("Entering getIdType method in CustomerDAO");
		List<Object[]> lst = null;
		JSONArray jsonArray = new JSONArray();
		String qry = "select key_2 key, par_desc value,status from ca840pb where key_1='IDENITFICATION_IND_LIST'";
		try {
			Query query = em.createNativeQuery(qry);
			lst = (List<Object[]>) query.getResultList();
			for (Object[] rs : lst) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("key", rs[0].toString());
				jsonObj.put("value", rs[1].toString());
				jsonArray.add(jsonObj);
				jsonObj = null;
			}
		} catch (Exception e) {
			logger.debug("Exception occured while fetching Id type Details", e);
			logger.error("Exception occured while fetching Id type Details", e);
		} finally {
			if (em != null)
				em.close();
		}
		logger.info("Exiting getIdType method in CustomerDAO");
		return jsonArray;
	}
	public JSONObject saveDependentDetails(JSONObject dependentInputObj) {
		return null;
	}
}
