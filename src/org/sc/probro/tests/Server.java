package org.sc.probro.tests;

import java.util.*;
import java.util.regex.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
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
	
	public boolean adminReset() throws IOException { 
		Response<String> response = httpPost(url("/reset"), new JSONObject(), String.class);
		return response.responseCode == 200;
	}

	public URL url(String path) throws MalformedURLException { 
		return new URL(base, path); 
	}
	
	public static boolean isSubclass(Class c1, Class c2) { 
		return c2.isAssignableFrom(c1);
	}
	
	public static class Response<T> { 
		public int responseCode;
		public T response;
		public String msg;
		
		public Response(int code, T v) { 
			this(code, v, null);
		}
		
		public Response(int code, T v, String m) { 
			responseCode = code;
			response = v;
			msg = m;
		}
	}
	
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_HTML = "text/html";
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
	
	public static <T> Response<T> httpPost(URL url, Object postObj, Class<T> format) throws IOException {
		
		String contentType = CONTENT_TYPE_FORM; 
		Class postClass = postObj.getClass();

		if(isSubclass(postClass, JSONObject.class)) { 
			contentType = CONTENT_TYPE_JSON;
		} else if (isSubclass(postClass, String.class)) { 
			contentType = CONTENT_TYPE_FORM;
		} else { 
			throw new IllegalArgumentException(String.format(
					"Unrecognized content format: %s", format.getSimpleName()));
		}

		HttpURLConnection cxn = (HttpURLConnection) url.openConnection();
		cxn.setRequestMethod("POST");
		cxn.setDoOutput(true);
		cxn.setRequestProperty("Content-Type", contentType);

		OutputStream os = cxn.getOutputStream();
		PrintStream ps = new PrintStream(os);
		ps.println(postObj.toString());		
		ps.close();

		cxn.connect();
		int responseCode = cxn.getResponseCode();
		String content = "";
		String error = null;
		
		try { 
			StringBuilder sb = new StringBuilder();
			InputStream is = cxn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int c ;
			while((c = isr.read()) != -1) { sb.append((char)c); }
			isr.close();

			content = sb.toString();
			
		} catch(IOException e) {
			error = cxn.getResponseMessage();
		}
		
		return new Response<T>(responseCode, convertContent(content, format), error);
	}
	
	public static <T> Response<T> httpGet(java.net.URL url, Class<T> format) throws IOException { 
		HttpURLConnection cxn = (HttpURLConnection) url.openConnection();
		cxn.connect();
		int responseCode = cxn.getResponseCode();
		String error = null;
		String content = "";

		try { 
			InputStream is = cxn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			StringBuilder sb = new StringBuilder();
			int readchar = -1;
			while((readchar = isr.read()) != -1) { 
				sb.append((char)readchar);
			}
			content = sb.toString();
			isr.close();
		} catch(IOException e) { 
			error = cxn.getResponseMessage();
		}

		return new Response<T>(responseCode, convertContent(content, format), error);
	}

	private static <T> T convertContent(String content, Class<T> format) { 
		try { 
			if(isSubclass(format, String.class)) {

				return (T)content;

			} else if (isSubclass(format, JSONObject.class)) { 
				if(content.trim().length() == 0) { return (T)null; }
				return (T) (new JSONObject(content));

			} else if (isSubclass(format, JSONArray.class)) {
				if(content.trim().length() == 0) { return (T)null; }
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
	
	public URL userURL(int userID) { 
		try { 
			return url(String.format("/user/%d/", userID));
		} catch(MalformedURLException e) { 
			throw new IllegalStateException(e);
		}				
	}

	public URL ontologyURL(int ontologyID) { 
		try { 
			return url(String.format("/ontology/%d/", ontologyID));
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
	
	public class RequestExample extends JSONObject {
		
		public RequestExample() { 
			super();
			try {
				put("search_text", "foo");
				put("context", "bar blah blah blah");
				put("provenance", "http://example.com/blah");

				put("ontology", ontologyURL(1).toString());
				
				append("metadata", new MetadataExample(this, "akey", "bvalue"));
				append("metadata", new MetadataExample(this, "ckey", "dvalue"));
				
			} catch (JSONException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public class LinkExample extends JSONObject {
		
		public LinkExample(String txt, String urlFragment) throws MalformedURLException { 
			this(txt, url(urlFragment));
		}
		
		public LinkExample(String txt, URL href) { 
			try {
				put("text", txt);
				put("href", href.toString());
			} catch (JSONException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public class MetadataExample extends JSONObject {
		
		public MetadataExample() { 
			this(null, "foo", "bar");
		}
		
		public MetadataExample(RequestExample req, String key, String value) { 
			super();
			try {
				put("metadata_key", key);
				put("metadata_value", value);
				
			} catch (JSONException e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
