package org.sc.probro.tests;

import java.util.*;
import java.util.regex.*;
import java.net.*;

public class Server {
	
	private URL base;
	private String host;
	private int port;
	
	public Server(String h, int p) throws MalformedURLException {
		host = h;
		port = p;
		base = new URL(String.format("http://%s:%d/", host, port));
	}
	
	public Server(String propName) throws MalformedURLException { 
		ResourceBundle bundle = ResourceBundle.getBundle(String.format("org.sc.probo.tests.%s", propName));
		host = bundle.getString("host");
		port = Integer.parseInt(bundle.getString("port"));
		base = new URL(String.format("http://%s:%d/", host, port));		
	}

	public URL url(String path) throws MalformedURLException { 
		return new URL(base, path); 
	}
	
	public URL requestsURL() { 
		try { 
			return url("/requests");
		} catch(MalformedURLException e) { 
			throw new IllegalStateException(e);
		}
	}

	public URL requestURL(String id) { 
		try { 
			return url(String.format("/request/%s", id));
		} catch(MalformedURLException e) { 
			throw new IllegalStateException(e);
		}
	}
	
	public URL usersURL() { 
		try { 
			return url("/users");
		} catch(MalformedURLException e) { 
			throw new IllegalStateException(e);
		}		
	}

	public URL ontologiesURL() { 
		try { 
			return url("/ontologies");
		} catch(MalformedURLException e) { 
			throw new IllegalStateException(e);
		}		
	}
}
