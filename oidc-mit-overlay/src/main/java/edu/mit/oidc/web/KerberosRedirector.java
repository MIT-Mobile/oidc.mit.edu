package edu.mit.oidc.web;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class KerberosRedirector {

	@Autowired
	private ConfigurationPropertiesBean config;
	
	@RequestMapping("kerberos_login")
	public String redirectToTarget(@RequestParam("target") String target) {
		if (target.startsWith(config.getIssuer())) {
			return "redirect:" + target;
		} else {
			return "redirect:/";
		}
	}
	
	
}
