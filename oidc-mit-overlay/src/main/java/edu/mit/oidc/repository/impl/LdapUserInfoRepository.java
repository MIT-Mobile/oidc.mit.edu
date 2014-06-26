package edu.mit.oidc.repository.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.NotImplementedException;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class LdapUserInfoRepository implements UserInfoRepository {

	private LdapTemplate ldapTemplate;
	private MessageDigest digest;
	
	// result cache
	private LoadingCache<String, UserInfo> cache;
	
	private AttributesMapper attributesMapper = new AttributesMapper() {
		@Override
		public Object mapFromAttributes(Attributes attr) throws NamingException {
			
			if (attr.get("uid") == null || attr.get("uidnumber") == null) {
				return null; // we can't go on if there's no UID
			}
			
			UserInfo ui = new DefaultUserInfo();

			// save the UID as the preferred username
			ui.setPreferredUsername(attr.get("uid").get().toString());
			
			// TODO: hash the UID number to get the sub
			String sub = Hex.encodeHexString(digest.digest(attr.get("uidnumber").get().toString().getBytes()));
			ui.setSub(sub);
			/*
			// but for now just use the UID as the sub
			ui.setSub(attr.get("uid").get().toString());
			 */
			
			
			
			//
			// everything else is optional
			//
			
			// email address
			if (attr.get("mail") != null) {
				ui.setEmail(attr.get("mail").get().toString());
				ui.setEmailVerified(true);
			}

			// phone number
			if (attr.get("telephoneNumber") != null) {
				ui.setPhoneNumber(attr.get("telephoneNumber").get().toString());
				ui.setPhoneNumberVerified(true);
			}
			
			// name structure
			if (attr.get("displayName") != null) {
				ui.setName(attr.get("displayName").get().toString());
			}
			
			if (attr.get("givenName") != null) {
				ui.setGivenName(attr.get("givenName").get().toString());
			}
			if (attr.get("sn") != null) {
				ui.setFamilyName(attr.get("sn").get().toString());
			}
			if (attr.get("initials") != null) {
				ui.setMiddleName(attr.get("initials").get().toString());
			}
			
			return ui;
		}
	};
	
	private CacheLoader<String, UserInfo> cacheLoader = new CacheLoader<String, UserInfo>() {

		@Override
		public UserInfo load(String username) throws Exception {
			Filter find = new EqualsFilter("uid", username);
			List res = ldapTemplate.search("", find.encode(), attributesMapper);
			
			if (res.isEmpty()) {
				throw new IllegalArgumentException("Unable to load uid: " + username);
			} else if (res.size() == 1) {
				return (UserInfo) res.get(0);
			} else {
				throw new IllegalArgumentException("Unable to load uid: " + username);
			}
		}

	};

	public LdapUserInfoRepository() {
		 try {
			this.digest = MessageDigest.getInstance("MD5");
		 } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 
		 this.cache = CacheBuilder.newBuilder()
				 	.maximumSize(100)
				 	.expireAfterAccess(14, TimeUnit.DAYS)
				 	.build(cacheLoader);
		
		 
	}
	
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
		//return getByUsername(sub);
		throw new NotImplementedException("Unable to search by subject in this repostory.");
	}

	@Override
	public UserInfo getByUsername(String username) {
		try {
			UserInfo ui = cache.get(username);
			return ui;
		} catch (UncheckedExecutionException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
	}

	@Override
	public void remove(UserInfo userInfo) {
		// read-only repository, unimplemented

	}

	@Override
	public UserInfo save(UserInfo userInfo) {
		// read-only repository, unimplemented no-op
		return userInfo;
	}

}
