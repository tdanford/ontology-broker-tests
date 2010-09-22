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
	
	@org.junit.Test 
	public void testGetOntologies() throws IOException { 
		URL ontologiesURL = server.ontologiesURL();
		JSONObject obj = server.httpGet(ontologiesURL, JSONObject.class);

		assertTrue("Returned collection was null", obj != null);
		
		assertTrue("Schema doesn't contain an 'Ontology' type", schemas.containsType("Ontology"));
		JSONType ontologyType = schemas.lookupType("Ontology");
		
		Iterator keys = obj.keys();
		while(keys.hasNext()) { 
			String key = (String)keys.next();
			try {
				JSONObject ont = obj.getJSONObject(key);
				assertTrue(
						String.format("%s is not of Ontology Type", ont.toString()),
						ontologyType.contains(ont));
				
			} catch (JSONException e) {
				throw new IllegalArgumentException(String.valueOf(key));
			}
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

