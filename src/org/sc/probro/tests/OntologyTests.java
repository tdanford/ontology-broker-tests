package org.sc.probro.tests;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Before;

import java.io.IOException;
import java.util.*;

public class OntologyTests {
	
	private Server server;
	
	@Before
	public void setup() { 
		
	}
	
	@org.junit.Test 
	public void testGetOntologies() throws IOException { 
		String server = ServerTests.server;

		String ontologiesURL = server+"/ontologies";
		JSONObject obj = ServerTests.httpGET_JSONObject(ontologiesURL);
		assertTrue(
				String.format("Received null response from %s", ontologiesURL),
				obj != null);
		
		
	}
}
