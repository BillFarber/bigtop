package com.markLogic.bigTop.middle.properties;

public class SecurityProperties {
	private String userDN;
	private String password;
	private String userdnPattern;
	private String baseDN;
	private String baseUrl;

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getUserdnPattern() {
		return userdnPattern;
	}

	public String getPassword() {
		return password;
	}

	public String getUserDN() {
		return userDN;
	}

	public String getBaseDN() {
		return baseDN;
	}

	SecurityProperties(String userDN, String password, String userdnPattern, String baseDN, String baseUrl) {
		this.userDN = userDN;
		this.password = password;
		this.userdnPattern = userdnPattern;
		this.baseDN = baseDN;
		this.baseUrl = baseUrl;
	}
}