package com.intellect.fna.service;

import net.sf.json.JSONArray;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intellect.fna.repositories.AlertsDAO;

@Service
public class AlertsBO {

	@Autowired
	AlertsDAO alertsDAO;

	public JSONArray getAlertsCount(JSONObject alertInputData)
			throws JSONException {
		net.sf.json.JSONArray alertsOutData = new JSONArray();
		try {
			alertsOutData = alertsDAO.getAlertsCount(alertInputData);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return alertsOutData;
	}
}