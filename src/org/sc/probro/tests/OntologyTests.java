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

public class OntologyTests {
	
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
	public void testGetOntologies() throws IOException { 
		URL ontologiesURL = server.ontologiesURL();
		
		assertTrue(schemas.containsType("Response"));
		JSONType responseType = schemas.lookupType("Response");
		
		assertTrue(schemas.containsType("Ontology"));
		JSONType ontologyType = schemas.lookupType("Ontology");

		JSONObject response = server.httpGet(ontologiesURL, JSONObject.class);
		
		assertTrue("Poorly formed or null response", response != null);
		assertTrue(error(response, responseType.explain(response)), 
				responseType.contains(response));

		try {
			JSONArray responseArray = response.getJSONArray("vals");

			for(int i = 0; i < responseArray.length(); i++) { 
				JSONObject entry = responseArray.getJSONObject(i);
				
				assertTrue(
						error(entry.toString(), ontologyType.explain(entry)),
						ontologyType.contains(entry));

			}
		} catch (JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

class BrokerSchemaEnv extends SchemaEnv { 
	public BrokerSchemaEnv() { 
		super(new File("docs/json-schemas/"));
	}
	
	public JSONType getRequestType() { return lookupType("Request"); } 
	public JSONType getMetadataType() { return lookupType("Metadata"); } 
	public JSONType getUserType() { return lookupType("User"); }
	public JSONType getOntologyType() { return lookupType("Ontology"); }
	public JSONType getSearchResultType() { return lookupType("SearchResult"); }
	public JSONType getLinkType() { return lookupType("Link"); }
}

