package com.intellect.fna.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository
public class AlertsDAO {
	@PersistenceContext(unitName="fna")
	EntityManager em;
	public JSONArray getAlertsCount( JSONObject alertInputData) throws Exception
	{
		net.sf.json.JSONArray alertCount = null;
		String selectCondition="";
		String groupCondition="";
		String whereCondition="";
		List<Object[]> countLst=null;
		try{
			alertCount = new net.sf.json.JSONArray();
			String custType = alertInputData.getString("custType");
			String rmId = alertInputData.getString("rmId");
			String custNo="";
			if(!custType.equalsIgnoreCase("RM")){
				selectCondition = " ,dtl.cust_no ";
				groupCondition = " ,dtl.cust_no ";
				whereCondition = " AND dtl.cust_no=:custNo ";
				custNo=alertInputData.getString("custNo");
			}
			StringBuffer queryBuffer = new StringBuffer();
			queryBuffer.append(" SELECT dtl.RM_ID,count(*) ");
			queryBuffer.append(selectCondition);
			queryBuffer.append(" FROM IWM_RMW_ALERT_MST mst, IWM_RMW_ALERT_DTL dtl WHERE ");
			queryBuffer.append(" mst.cat_code = dtl.cat_code AND mst.sub_cat_code = dtl.sub_cat_code AND mst.br_cd = dtl.br_cd");
			queryBuffer.append(" AND dtl.RM_ID=:rmId ");
			queryBuffer.append(whereCondition);
			queryBuffer.append(" GROUP BY dtl.RM_ID ");
			queryBuffer.append(groupCondition);

			Query alertCountQuery = em.createNativeQuery(queryBuffer.toString());
			alertCountQuery.setParameter("rmId", rmId);
			if(!custType.equalsIgnoreCase("RM"))
					alertCountQuery.setParameter("custNo", custNo);
			countLst = alertCountQuery.getResultList();
			for(Object[] obj:countLst){
				net.sf.json.JSONObject jobj = new net.sf.json.JSONObject();
				jobj.put("RM_ID", obj[0].toString());
				jobj.put("ALERTCOUNT", obj[1].toString());
				if(!custType.equalsIgnoreCase("RM"))
					jobj.put("CUST_NO", obj[2].toString());
				alertCount.add(jobj);
			}
			
		}catch(Exception e){
			System.out.println("Exception in fetching alertCount : "+e);
		}
		return alertCount;
	}
}
