package com.intellect.fna.rest.controllers.aggregators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin
public class AggregatorController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Environment env;
	
	@RequestMapping(value="/getAllDetails",method=RequestMethod.POST,produces= {MediaType.APPLICATION_JSON})
    public String getAllDetails(@RequestBody JSONObject inputObj,HttpServletRequest httpRequest) {
		JSONObject finalJSON = new JSONObject();
		Client client = ClientBuilder.newBuilder().build();
		String []endpointsArr=env.getProperty("services.customer-service.end-points").split(",");
		String []responsesArr=env.getProperty("services.customer-service.response-names").split(",");
		List<Future<Response>> futureResponseList=new ArrayList<>(endpointsArr.length);
		for(String endPoint:endpointsArr)
		{
			WebTarget target = client.target(env.getProperty("services.customer-service.host-name")+env.getProperty("services.customer-service.context-path")+endPoint);
			Invocation.Builder reqBuilder = target.request();
			AsyncInvoker asyncInvoker = reqBuilder.async();
			Future<Response> responseFuture = asyncInvoker.post(Entity.json(inputObj));
			futureResponseList.add(responseFuture);
		}
		int count=0;
		for(Future<Response> futureResponse:futureResponseList)
		{
			try {
				finalJSON.put(responsesArr[count], futureResponse.get().readEntity(String.class));
			} catch (InterruptedException | ExecutionException e) {
				logger.debug("Exception Occured ",e);
			}
			count++;
		}
		return finalJSON.toString();
    }
}
