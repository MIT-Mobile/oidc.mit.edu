package edu.mit.oidc.userdetails;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class UsernamePrefixKerberosLdapUserDetailsService extends LdapUserDetailsService {

	private static Logger logger = LoggerFactory.getLogger(UsernamePrefixKerberosLdapUserDetailsService.class);	
	
	public UsernamePrefixKerberosLdapUserDetailsService(LdapUserSearch userSearch) {
		super(userSearch);
		// TODO Auto-generated constructor stub
	}

	public UsernamePrefixKerberosLdapUserDetailsService(
			LdapUserSearch userSearch,
			LdapAuthoritiesPopulator authoritiesPopulator) {
		super(userSearch, authoritiesPopulator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		List<String> parts = Lists.newArrayList(Splitter.on("@").split(username));
		
		if (parts.size() == 2) {
			// we've got a USER @ DOMAIN type of thing going on here
			String user = parts.get(0);
			String domain = parts.get(1);
			
			logger.info("Found valid user format: " + user + "  @  " + domain);
			return super.loadUserByUsername(user);
		} else {
			throw new UsernameNotFoundException("Could not find malformed username: " + username);
		}
	}
	
	

}
