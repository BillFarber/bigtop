package com.markLogic.bigTop.middle;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public DefaultSpringSecurityContextSource ldapContextSource() {
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource("ldap://freeipa.bigtop.local:389/dc=bigtop,dc=local");
		contextSource.setUserDn("uid=admin,cn=users,cn=accounts,dc=bigtop,dc=local");
		contextSource.setPassword("december");
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
			.formLogin().defaultSuccessUrl("/");
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.eraseCredentials(false);

		DefaultSpringSecurityContextSource ldapContextSource = ldapContextSource();		
		LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> ldapAuthenticationProviderConfigurer = auth.ldapAuthentication();
		ldapAuthenticationProviderConfigurer
			.userDnPatterns("uid={0},cn=users,cn=accounts")
			.contextSource(ldapContextSource);

		auth.apply(ldapAuthenticationProviderConfigurer);
	}

	@Bean
	public DefaultSpringSecurityContextSource contextSource() {
		return  new DefaultSpringSecurityContextSource(Arrays.asList("ldap://freeipa.bigtop.local:389/"), "dc=bigtop,dc=local");
	}

}