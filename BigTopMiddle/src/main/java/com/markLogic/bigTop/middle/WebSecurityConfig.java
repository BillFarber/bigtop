package com.markLogic.bigTop.middle;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.markLogic.bigTop.middle.properties.SecurityProperties;
import com.markLogic.bigTop.middle.properties.SecurityPropertiesFactory;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static SecurityProperties securityProperties;
	
	static {
		try {
			securityProperties = SecurityPropertiesFactory.createSecurityProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Bean
	public DefaultSpringSecurityContextSource ldapContextSource() {
		String providerURL = securityProperties.getBaseUrl() + securityProperties.getBaseDN();
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(providerURL);
		contextSource.setUserDn(securityProperties.getUserDN());
		contextSource.setPassword(securityProperties.getPassword());
		contextSource.setReferral("follow");
		contextSource.afterPropertiesSet();
		return contextSource;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().fullyAuthenticated()
				.and()
			.formLogin().defaultSuccessUrl("/")
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login");
		}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.eraseCredentials(false);

		DefaultSpringSecurityContextSource ldapContextSource = ldapContextSource();		
		LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> ldapAuthenticationProviderConfigurer = auth.ldapAuthentication();
		ldapAuthenticationProviderConfigurer
			.userDnPatterns(securityProperties.getUserdnPattern())
			.contextSource(ldapContextSource);

		auth.apply(ldapAuthenticationProviderConfigurer);
	}

	@Bean
	public DefaultSpringSecurityContextSource contextSource() {
		return  new DefaultSpringSecurityContextSource(Arrays.asList(securityProperties.getBaseUrl()), securityProperties.getBaseDN());
	}

}