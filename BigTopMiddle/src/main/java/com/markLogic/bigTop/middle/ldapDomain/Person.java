package com.markLogic.bigTop.middle.ldapDomain;

import javax.naming.Name;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

/**
 *
 * Stuff returned by LDAP
 * 
 * Person Attribute ID: givenName
 * Person Attribute ID: initials 
 * Person Attribute ID: sn 
 * Person Attribute ID: loginShell 
 * Person Attribute ID: gidNumber 
 * Person Attribute ID: uidNumber 
 * Person Attribute ID: displayName 
 * Person Attribute ID: objectClass 
 * Person Attribute ID: uid 
 * Person Attribute ID: gecos 
 * Person Attribute ID: cn 
 * Person Attribute ID: homeDirectory
 * 
 */
@Entry(objectClasses = { "person" })
public final class Person {
	@Id
	private Name dn;

	@Attribute(name = "uid")
	private String uid;

	@Attribute(name = "givenName")
	private String firstName;

	@Attribute(name = "sn")
	private String lastName;

	@Attribute(name = "displayName")
	private String fullName;

	@Attribute(name = "cn")
	private String cn;

	public Name getDn() {
		return dn;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getFullName() {
		return fullName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirestName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
