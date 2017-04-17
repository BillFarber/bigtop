package com.markLogic.bigTop.middle.marklogic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.middle.marklogic.domain.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.JacksonDatabindHandle;

public class MarkLogicClientFactory {

	private static final Logger logger = LoggerFactory.getLogger(MarkLogicClientFactory.class);
	
    private static String ML_HOST;
    private static Integer ML_REST_PORT;
    private static Authentication ML_AUTHENTICATION;
    private final static SSLContext sslContext = BigTopSSLContextFactory.getBigTopSSLContext();

    private static ObjectMapper mapper = new ObjectMapper();
    
    private MarkLogicClientFactory() {}

    public static DatabaseClient createMarkLogicClient(String username, String password) throws IOException {
    	DatabaseClient client = null;
    	if (getMarkLogicProperties()) {
    		DatabaseClientFactory.getHandleRegistry().register(JacksonDatabindHandle.newFactory(mapper, Product.class));
    		client = DatabaseClientFactory.newClient(ML_HOST, ML_REST_PORT, username, password, ML_AUTHENTICATION, sslContext);
    		logger.info("Created MarkLogic client for " + username);
    	} else {
    		throw new IOException();
    	}
    	return client;
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
