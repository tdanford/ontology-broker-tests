package org.sc.probro.tests;

import java.io.File;

import tdanford.json.schema.JSONType;
import tdanford.json.schema.SchemaEnv;

public class BrokerSchemaEnv extends SchemaEnv { 
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
