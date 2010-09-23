package org.sc.probro.tests;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

import tdanford.json.JSONUtils;
import tdanford.json.schema.JSONType;
import tdanford.json.schema.SchemaEnv;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MakeRequestTest {
	
	private static String RequestTypeName = "Request";
	
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
	public void testCreateRequest() throws IOException, JSONException {
		
		assertTrue(
				String.format("Couldn't reset server."),
				server.adminReset());
		
		URL requestsURL = server.requestsURL();
		JSONObject req = server.new RequestExample();
		
		Server.Response<JSONObject> resp = server.httpPost(requestsURL, req, JSONObject.class);
		assertTrue(
				String.format("Response %d: \"%s\"", resp.responseCode, resp.msg),
				resp.responseCode == 200);
		
		assertTrue(schemas.lookupType("ProvisionalTerm").contains(resp.response));
		
		String term = resp.response.getString("term");
		assertTrue(term != null && term.length() > 0);
		
		URL newRequestURL = server.url(term);
		
		Server.Response<JSONObject> reqResp = server.httpGet(newRequestURL, JSONObject.class);
		
		assertTrue(
				String.format("Response %d (%s)", reqResp.responseCode, reqResp.msg),
				reqResp.responseCode == 200);

		assertTrue(schemas.lookupType("Request").explain(reqResp.response),
				schemas.lookupType("Request").contains(reqResp.response));
	}
}


