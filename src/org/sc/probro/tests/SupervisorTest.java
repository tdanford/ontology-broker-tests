package org.sc.probro.tests;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import tdanford.json.schema.JSONType;
import tdanford.json.schema.SchemaEnv;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class SupervisorTest {
	
	private Server server;
	private BrokerSchemaEnv schemas;
	
	@Before
	public void setup() {
		schemas = new BrokerSchemaEnv();
		try {
			server = new Server();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static String error(Object obj, String explain) { 
		return String.format("%s => %s", String.valueOf(obj), explain);
		//return String.valueOf(obj);
	}
	
	@org.junit.Test 
	public void testGetSupervisor() throws IOException { 
		URL supervisorURL = server.supervisorURL();
		
		assertTrue(schemas.containsType("Supervisor"));
		JSONType responseType = schemas.lookupType("Supervisor");
		
		JSONObject response = server.httpGet(supervisorURL, JSONObject.class).response;
		
		assertTrue("Poorly formed or null response", response != null);
		assertTrue(error(response, responseType.explain(response)), 
				responseType.contains(response));
	}
}



