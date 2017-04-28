package com.markLogic.bigTop.middle.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecurityPropertiesFactory {

	public static SecurityProperties createSecurityProperties() throws IOException {
		Properties properties = new Properties();
		InputStream input = null;
		SecurityProperties securityProperties = null;
		
		try {
			input = PropertiesHelper.getResourceAsStream("ldap.properties");
			properties.load(input);
			String userDN = properties.getProperty("userdn");
			String password = properties.getProperty("password");
			String userdnPattern = properties.getProperty("userdn.pattern");
			String baseDN = properties.getProperty("basedn");
			String baseUrl = properties.getProperty("baseurl");
			securityProperties = new SecurityProperties(userDN, password, userdnPattern, baseDN, baseUrl);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return securityProperties;
	}
}
