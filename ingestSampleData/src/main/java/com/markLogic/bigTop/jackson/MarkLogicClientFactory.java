package com.markLogic.bigTop.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.JacksonDatabindHandle;

public class MarkLogicClientFactory {
	
    private static String ML_HOST;
    private static Integer ML_REST_PORT;
    private static String ML_USERNAME;
    private static String ML_PASSWORD;
    private static Authentication ML_AUTHENTICATION;
    private static DatabaseClient client = null;

    private static SSLContext sslContext = BigTopSSLContextFactory.getBigTopSSLContext();

    private static ObjectMapper mapper = new ObjectMapper();
    
    public static DatabaseClient getMarkLogicClient() throws IOException {
    	if (client == null) {
    		createMarkLogicClient();
    	}
    	return client;
    }
    
    private static void createMarkLogicClient() throws IOException {
    	if (getMarkLogicProperties()) {
    		DatabaseClientFactory.getHandleRegistry().register(JacksonDatabindHandle.newFactory(mapper, Product.class));
    		client = DatabaseClientFactory.newClient(ML_HOST, ML_REST_PORT, ML_USERNAME, ML_PASSWORD, ML_AUTHENTICATION, sslContext);
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
		    ML_USERNAME = properties.getProperty("username");
		    ML_PASSWORD = properties.getProperty("password");
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
