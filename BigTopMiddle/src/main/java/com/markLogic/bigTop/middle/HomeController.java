package com.markLogic.bigTop.middle;

import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
public class HomeController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	@GetMapping("/")
	public String index() throws javax.naming.NamingException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String password = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		System.out.println("username: " + username);
		System.out.println("password: " + password);

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		List<Person> persons = getAllPersonNames(ldapTemplate, username);
		for (Person person : persons) {
			System.out.println("person: " + person);
		}
		
		checkForMembership(ldapTemplate, username);

		PullJsonFromMarkLogic obj = new PullJsonFromMarkLogic(username, password);
		List<String> resultUris = obj.search("");
		StringBuilder resultDiv = new StringBuilder("<div>Search Results<ol>");
		for (String uri : resultUris) {
			resultDiv.append("<li>"+uri+"</li>");
		}
		resultDiv.append("</ol></div>");
		String welcome = "<h1>Welcome to the home page " + persons.get(0).getFullName() + "!</h1>";
		String logout = "<div><a href='/logout'>Logout</a></div>";
		return "<html><body>"+welcome+resultDiv.toString()+logout+"</body></html>";
	}

	private void checkForMembership(LdapTemplate ldapTemplate, String uid) {
		List<String> groupNames = ldapTemplate.search("", "(&(objectClass=ipausergroup)(cn=bigtopusers)(member=*))", new GroupAttributesMapper());
		System.out.println("Groups Found: " + groupNames.size());
		groupNames = ldapTemplate.search("", "(&(objectClass=ipausergroup)(cn=bigtopusers)(member=uid="+uid+",cn=users,cn=accounts,dc=bigtop,dc=local))", new GroupAttributesMapper());
		System.out.println("Groups Found: " + groupNames.size());
		groupNames = ldapTemplate.search("", "(&(objectClass=ipausergroup)(cn=bigtopusers)(member=uid="+uid+",cn=users,cn=accounts,dc=bigtop,dc=local))", new GroupAttributesMapper());
		System.out.println("Groups Found: " + groupNames.size());
	}
	
	public List<Person> getAllPersonNames(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.search("", "(&(objectClass=person)(uid="+uid+"))", new PersonAttributesMapper());
	}
	
	private class PersonAttributesMapper implements AttributesMapper<Person> {
		public Person mapFromAttributes(Attributes attrs) throws NamingException, javax.naming.NamingException {
            Attribute memberOf1 = attrs.get("memberOf");
            if (memberOf1 != null) {
            	System.out.println("memberOf is NOT NULL");
            	System.out.println("memberOf Size: " + memberOf1.size());
            } else {
            	System.out.println("memberOf is NULL");
            }
            	
            NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
			while (attributes.hasMore()) {
				Attribute att = attributes.next();
				System.out.println("Person Attribute ID: " + att.getID());
			}
			Person person = new Person();
			person.setFullName((String) attrs.get("cn").get());
			person.setLastName((String) attrs.get("sn").get());
			return person;
		}
	}

	public List<String> getGroups(LdapTemplate ldapTemplate) {
		System.out.println("getGroups");
	    AndFilter filter = new AndFilter();
	    filter.and(new EqualsFilter("objectclass", "ipausergroup"));
		return ldapTemplate.search("", filter.encode(), new GroupAttributesMapper());
	}

	private class GroupAttributesMapper implements AttributesMapper<String> {
		public String mapFromAttributes(Attributes attrs) throws NamingException, javax.naming.NamingException {
			NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
			while (attributes.hasMore()) {
				Attribute att = attributes.next();
				System.out.println("Group Attribute ID: " + att.getID());
			}
			String groupName = (String) attrs.get("cn").get();
			System.out.println("groupName: " + groupName);
			return groupName;
		}
	}
}
