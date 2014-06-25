<%@page import="org.springframework.security.web.savedrequest.HttpSessionRequestCache"%>
<%@page import="org.springframework.security.web.savedrequest.SavedRequest"%>
<%@page import="org.springframework.web.util.UriUtils"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%

String url = "/";

SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

if (savedRequest != null) {
	url = UriUtils.encodeQueryParam(savedRequest.getRedirectUrl(), "UTF-8");
}				
				
%>
<o:header title="Log In" />
<script type="text/javascript">
<!--

$(document).ready(function() {
	$('#j_username').focus();
});

//-->
</script>
<o:topbar />
<div class="container-fluid main">

	<h1>Log In</h1>

	<c:if test="${ param.error != null }">
		<div class="alert alert-error">The system was unable to log you in. Please try again.</div>
	</c:if>


<div class="row-fluid">
      <div class="span4 well">
       <h2>Log in with Kerberos username and password</h2>
	   <form action="<%=request.getContextPath()%>/j_spring_security_check" method="POST">
	   	<div>
         <div class="input-prepend input-append input-block-level">
         	<span class="add-on"><i class="icon-user"></i></span>
         	<input type="text" placeholder="Username" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" value="" id="j_username" name="j_username">
         	<span class="add-on">@mit.edu</span>
         	</div>
        </div>
        <div>
         <div class="input-prepend input-block-level">
         	<span class="add-on"><i class="icon-lock"></i></span>
         	<input type="password" placeholder="Password" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" id="j_password" name="j_password">
         </div>
        </div>
        <div class="form-actions"><input type="submit" class="btn" value="Login" name="submit"></div>
	   </form>
      </div>
   
	<div class="span4 well">
		<h2>Log in with Kerberos</h2>
		<a href="kerberos_login?target=<%= url %>" class="btn">Use Existing Kerberos Tickets</a>
	</div>

	<div class="span4 well">
		<h2>Log in with Certificate</h2>
		<a href="cert_login?target=<%= url %>" class="btn">Use MIT Certificate</a>
	</div>

</div>

</div>
<o:footer/>
