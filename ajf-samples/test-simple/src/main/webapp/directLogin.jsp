<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page import="java.lang.*"%>
<html>
	<head>
		<title>Simple TEST V2</title>
	</head>
	<body>
		<h1>Direct Login</h1>
		<h2>User: <%=request.getRemoteUser()%></h2>
		<input type="text" id="myUsername"/>
		<input type="password" id="myPassword"/>
		<input type="submit" value="Login" onclick="doLogin('myUsername','myPassword','securizedIndex.jsp','error_login.jsp');">
		<a href="test">Navigate to Test</a>
	</body>	
	
<script type="text/javascript"> 

	//securizedIndex.jsp
	//error_login.jsp 

	function buildXMLHttpRequest() {
		var xmlHttpReq = null;

		// Mozilla/Safari
		if (window.XMLHttpRequest) {
		    xmlHttpReq = new XMLHttpRequest();
		}
		// IE
		else if (window.ActiveXObject) {
		    xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
		}
		return xmlHttpReq;
	}

	function postLoginForm(xmlHttpReq, usernameComponentId, passwordComponentId, securizedURI, loginErrorURI) {
		
		jUserName = document.getElementById(usernameComponentId).value;
		jPassword = document.getElementById(passwordComponentId).value;
		
		encodedURI = "j_security_check?j_username="+jUserName+"&j_password="+encodeURIComponent(jPassword);
		alert(encodedURI);
				
		xmlHttpReq.onreadystatechange = function() {
			if (xmlHttpReq.readyState == 4 ) {
			 	alert(xmlHttpReq.status);
				// Authenticated
			 	if (xmlHttpReq.status == 200) {
				 	window.location = securizedURI;
				 	return;
				}
				// Authentication Failure
				if (xmlHttpReq.status == 403) {
					window.location = loginErrorURI;
					return
				}
				else {
					window.location = loginErrorURI;
					return;
				}
			}
		};		
		
		xmlHttpReq.open("POST", encodedURI, false);
		xmlHttpReq.send();
				
	}

	function doLogin(usernameComponentId, passwordComponentId, securizedURI, loginErrorURI) {
		
		var xmlHttpReq = buildXMLHttpRequest();

		xmlHttpReq.onreadystatechange = function() {
			if (xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200) {
				alert(xmlHttpReq.responseText);
				postLoginForm(xmlHttpReq, usernameComponentId, passwordComponentId, securizedURI, loginErrorURI);
				return;
			}
		};
		
		// Try to access securized resource
		xmlHttpReq.open("GET", securizedURI, false);
		xmlHttpReq.send();
		    
	}
	
	</script>
 
 
</html>