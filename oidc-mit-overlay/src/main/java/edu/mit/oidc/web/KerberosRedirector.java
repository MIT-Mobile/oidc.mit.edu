package edu.mit.oidc.web;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
/**
 * 
 * This simple controller holds a target URL passed in from the login page. 
 * 
 * The redirector MUST start with the configured issuer URL.
 * 
 * @author jricher
 *
 */
public class KerberosRedirector {

	@Autowired
	private ConfigurationPropertiesBean config;
	
	@RequestMapping({"kerberos_login", "cert_login"})
	public View redirectToTarget(@RequestParam("target") String target) {
		if (target.startsWith(config.getIssuer())) {
			return new RedirectView(target, false, false, false);
		} else {
			return new RedirectView("/", true, false, false);
		}
	}
	
	
}
