package edu.mit.oidc.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

public class LdapUserInfoRepository implements UserInfoRepository {

	private LdapTemplate ldapTemplate;
	
	private AttributesMapper attributesMapper = new AttributesMapper() {
		@Override
		public Object mapFromAttributes(Attributes attr) throws NamingException {
			UserInfo ui = new DefaultUserInfo();
			
			// FIXME: sub shouldn't be the uid
			ui.setSub(attr.get("uid").get().toString());
			ui.setPreferredUsername(attr.get("uid").get().toString());
			ui.setName(attr.get("displayName").get().toString());
			ui.setEmail(attr.get("mail").get().toString());
			ui.setEmailVerified(true);
			ui.setGivenName(attr.get("givenName").get().toString());
			ui.setFamilyName(attr.get("sn").get().toString());
			ui.setMiddleName(attr.get("initials").get().toString());
			
			return ui;
		}
	};
	
	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	public Collection<? extends UserInfo> getAll() {
		return Collections.emptySet();
	}

	@Override
	public UserInfo getBySubject(String sub) {
		// FIXME: subject and username are the same (for now)
		return getByUsername(sub);
	}

	@Override
	public UserInfo getByUsername(String username) {
		Filter find = new EqualsFilter("uid", username);
		List res = ldapTemplate.search("", find.encode(), attributesMapper);
		
		if (res.isEmpty()) {
			return null;
		} else if (res.size() == 1) {
			return (UserInfo) res.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void remove(UserInfo userInfo) {
		// read-only repository, unimplemented

	}

	@Override
	public UserInfo save(UserInfo userInfo) {
		// read-only repository, unimplemented
		return getBySubject(userInfo.getSub());
	}

}
