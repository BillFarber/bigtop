package com.markLogic.bigTop.middle.marklogic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.middle.marklogic.domain.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.JacksonDatabindHandle;

// This is a singleton for now
// This really ought to be a session variable
public class MarkLogicClient {
	
    private static String ML_HOST;
    private static Integer ML_REST_PORT;
    private static Authentication ML_AUTHENTICATION;
    private static DatabaseClient client = null;

    private final static SSLContext sslContext = BigTopSSLContextFactory.getBigTopSSLContext();

    private static ObjectMapper mapper = new ObjectMapper();
    
    public static DatabaseClient getMarkLogicClient(String username, String password) throws IOException {
    	if (client == null) {
    		createMarkLogicClient(username, password);
    	}
    	return client;
    }
    
    public static void releaseMarkLogicClient() {
    	if (client != null) {
    		client.release();
    		client = null;
    	}
    }

    private static void createMarkLogicClient(String username, String password) throws IOException {
    	if (getMarkLogicProperties()) {
    		DatabaseClientFactory.getHandleRegistry().register(JacksonDatabindHandle.newFactory(mapper, Product.class));
    		client = DatabaseClientFactory.newClient(ML_HOST, ML_REST_PORT, username, password, ML_AUTHENTICATION, sslContext);
    	} else {
    		throw new IOException();
    	}
    }

	private static Boolean getMarkLogicProperties() {
		Boolean success = false;
		Properties properties = new Properties();
		InputStream input = null;
		
		try {
			input = getResourceAsStream("marklogic.properties");
			properties.load(input);
			// get the property value and print it out
		    ML_HOST = properties.getProperty("host");
		    ML_REST_PORT = Integer.valueOf(properties.getProperty("port"));
			ML_AUTHENTICATION = Authentication.valueOf(properties.getProperty("authentication"));
			success = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

    public static InputStream getResourceAsStream( String resource ) {
      final InputStream in = getContextClassLoader().getResourceAsStream( resource );
      return in == null ? getResourceAsStream( resource ) : in;
    }

    private static ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
    }
}
