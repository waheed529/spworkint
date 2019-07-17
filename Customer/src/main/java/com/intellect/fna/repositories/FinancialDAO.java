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
public class FinancialDAO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@PersistenceContext(unitName = "fna")
	EntityManager em;

	public JSONArray getDashboardHeader(String cif) throws Exception {
		List<Object[]> lst = null;

		JSONArray jsonArray = new JSONArray();

		String qry = "select  sum(a.current_value_br_ccy ) current_value,sum(a.investment_amt) investment_amt ,sum(a.r_gain) r_gain,sum(a.unrealized_gain) unrealized_gain from iwm_rmw_prod_dtls a where a.base_no=:cif ";
		try {
			Query query = em.createNativeQuery(qry);
			query.setParameter("cif", cif);
			lst = (List<Object[]>) query.getResultList();

			for (Object[] obj : lst) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("current_value",  obj[0] == null ? "" : obj[0].toString());
				jsonObj.put("investment_amt", obj[1] == null ? "" : obj[1].toString());
				jsonObj.put("r_gain", obj[2] == null ? "" : obj[2].toString());
				jsonObj.put("unrealized_gain", obj[3] == null ? "" : obj[3].toString());
				jsonArray.add(jsonObj);
				jsonObj = null;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (em != null)
				em.close();
		}
		return jsonArray;
	}

	public JSONArray getDashboardGraph(String cif) throws Exception {
		List<Object[]> lst = null;

		JSONArray jsonArray = new JSONArray();

		String qry = "select  sum(a.current_value_br_ccy ) current_value,a.asset_desc  from iwm_rmw_prod_dtls a  where a.base_no=:cif  group by a.asset_desc ";
		try {
			Query query = em.createNativeQuery(qry);
			query.setParameter("cif", cif);
			lst = (List<Object[]>) query.getResultList();

			for (Object[] obj : lst) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("current_value", obj[0] == null ? "" : obj[0].toString());
				jsonObj.put("asset_desc", obj[1] == null ? "" : obj[1].toString());
				jsonArray.add(jsonObj);
				jsonObj = null;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (em != null)
				em.close();
		}
		return jsonArray;
	}

	public JSONArray getDashboardHolding(String cif) throws Exception {
		List<Object[]> lst = null;

		JSONArray jsonArray = new JSONArray();

		String qry = "select  a.asset_desc, a.scheme_name ,'' sec_ccy,'0' current_value  from  iwm_rmw_prod_dtls a   where a.base_no=:cif  order by a.asset_desc ";

		try {
			Query query = em.createNativeQuery(qry);
			query.setParameter("cif", cif);
			lst = (List<Object[]>) query.getResultList();

			for (Object[] obj : lst) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("asset_desc", obj[0] == null ? "" : obj[0].toString());
				jsonObj.put("scheme_name", obj[1] == null ? "" : obj[1].toString());
				jsonObj.put("sec_ccy", obj[2] == null ? "" : obj[2].toString());
				jsonObj.put("current_value", obj[3] == null ? "" : obj[3].toString());
				jsonArray.add(jsonObj);
				jsonObj = null;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (em != null)
				em.close();
		}
		return jsonArray;
	}

	public JSONArray getDashboardTransactions(String cif) throws Exception {
		List<Object[]> lst = null;

		JSONArray jsonArray = new JSONArray();

		String qry = "select a.asset_cd ,a.security_name , a.security_ccy ,a.transaction_type,a.investment_amt ,nvl(a.purchased_on,a.sold_on) invDate from \r\n"
				+ "tb_pa_transactions_mst a where a.base_no=:cif";

		try {
			Query query = em.createNativeQuery(qry);
			query.setParameter("cif", cif);
			lst = (List<Object[]>) query.getResultList();

			for (Object[] obj : lst) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("asset_cd", obj[0] == null ? "" : obj[0].toString());
				jsonObj.put("security_name", obj[1] == null ? "" : obj[1].toString());
				jsonObj.put("security_ccy",obj[2] == null ? "" : obj[2].toString());
				jsonObj.put("transaction_type", obj[3] == null ? "" : obj[3].toString());
				jsonObj.put("investment_amt", obj[4] == null ? "" : obj[4].toString());
				jsonObj.put("invDate", obj[5] == null ? "" : obj[5].toString());
				jsonArray.add(jsonObj);
				jsonObj = null;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (em != null)
				em.close();
		}
		return jsonArray;
	}
}
