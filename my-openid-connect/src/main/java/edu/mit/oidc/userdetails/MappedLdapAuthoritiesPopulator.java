package edu.mit.oidc.userdetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import com.google.common.collect.ImmutableSet;

public class MappedLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private Set<String> admins = Collections.emptySet();
	
	private static final GrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");
	private static final GrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
	
	public Set<String> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<String> admins) {
		this.admins = admins;
	}

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
		
		if (admins.contains(username)) {
			return ImmutableSet.of(ROLE_ADMIN, ROLE_USER);
		} else {
			return ImmutableSet.of(ROLE_USER);
		}
		
	}

}
