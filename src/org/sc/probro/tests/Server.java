package org.sc.probro.tests;

import java.util.*;
import java.util.regex.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		ResourceBundle bundle = ResourceBundle.getBundle(String.format("org.sc.probro.tests.%s", propName));
		host = bundle.getString("host");
		port = Integer.parseInt(bundle.getString("port"));
		base = new URL(String.format("http://%s:%d/", host, port));		
	}
	
	public Server() throws MalformedURLException { 
		this("default");
	}

	public URL url(String path) throws MalformedURLException { 
		return new URL(base, path); 
	}
	
	public static boolean isSubclass(Class c1, Class c2) { 
		return c2.isAssignableFrom(c1);
	}
	
	public static <T> T httpGet(java.net.URL url, Class<T> format) throws IOException { 
		HttpURLConnection cxn = (HttpURLConnection) url.openConnection();
		cxn.connect();

		InputStream is = cxn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);

		StringBuilder sb = new StringBuilder();
		int readchar = -1;
		while((readchar = isr.read()) != -1) { 
			sb.append((char)readchar);
		}
		String content = sb.toString();
		isr.close();

		try { 
			if(isSubclass(format, String.class)) {

				return (T)content;

			} else if (isSubclass(format, JSONObject.class)) { 
				return (T) (new JSONObject(content));

			} else if (isSubclass(format, JSONArray.class)) {
				return (T) (new JSONArray(content));

			} else { 
				throw new IllegalArgumentException(String.format("Unrecognized format: %s" , format.getSimpleName()));
			}
		} catch(JSONException e) { 
			e.printStackTrace(System.err);
			System.err.println(String.format("---------\nContent:\n%s\n---------", content));
			//throw new IllegalArgumentException(e);
			return null;
		}

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
