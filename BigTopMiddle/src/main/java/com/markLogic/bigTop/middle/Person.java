/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markLogic.bigTop.middle;

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
public class Person {
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

	public String toHtmlDiv() {
		StringBuilder sb = new StringBuilder("<div>");
		sb.append("<ul>");
		sb.append("<li>"+this.dn+"</li>");
		sb.append("<li>"+this.firstName+"</li>");
		sb.append("<li>"+this.lastName+"</li>");
		sb.append("<li>"+this.fullName+"</li>");
		sb.append("<li>"+this.cn+"</li>");
		sb.append("<li>"+this.uid+"</li>");
		sb.append("</ul>");
		sb.append("</div>");
		return sb.toString();
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
