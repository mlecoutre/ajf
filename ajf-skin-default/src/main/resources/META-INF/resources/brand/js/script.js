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

function navigateTo(targetURI) {
	
	window.location.href = targetURI;
	//window.location.reload(true);
	
}

function printMessage(elementId, message) {
	var element = document.getElementById(elementId);
	element.innerHTML = "<b>" + message + "</b>";
}

function postLoginForm(xmlHttpReq, usernameComponentId, passwordComponentId, securizedURI, loginErrorURI) {
	
	jUserName = document.getElementById(usernameComponentId).value;
	jPassword = document.getElementById(passwordComponentId).value;
	
	encodedURI = "j_security_check?j_username="+jUserName+"&j_password="+encodeURIComponent(jPassword);
	
	xmlHttpReq.onreadystatechange = function() {
		if (xmlHttpReq.readyState == 4 ) {
			// Authenticated
		 	if (xmlHttpReq.status == 200) {
		 		if (-1 == xmlHttpReq.responseText.indexOf('authentication-failure')) {
		 			navigateTo(securizedURI);
		 		}
		 		else {
		 			printMessage('InLoginForm:loginMessage',
		 					'Authentication failure.');
		 			//navigateTo(loginErrorURI);
		 		}
			}
		 	else {
				// Authentication Failure
				if (xmlHttpReq.status == 403) {
					printMessage('InLoginForm:loginMessage',
						'Authentication failure.');
					//navigateTo(loginErrorURI);
				}
				else {
					//alert(xmlHttpReq.status);
					//alert(xmlHttpReq.responseText);
					printMessage('InLoginForm:loginMessage',
						'Authentication failure.');
					//navigateTo(loginErrorURI);
				}
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
			postLoginForm(xmlHttpReq, usernameComponentId, passwordComponentId, securizedURI, loginErrorURI);
		}
	};
	
	// Try to access securized resource
	xmlHttpReq.open("GET", securizedURI, false);
	xmlHttpReq.send();
	    
}

$(document).ready(function(){

	/****** Check browser to load CSS3 or JS *********/
	var br=new Array(4);
	br=getBrowser();
	var moz = ((br[0]).indexOf('firefox') != -1);
	var ie = ((br[0]).indexOf('msie') != -1);
	var mozLt4 = moz && br[1].substr(0,3) < 4;
	var ieLt10 = ie && br[1].substr(0,3) < 10;
	
	// SI Firefox < 4 ou IE < 10
//	if (mozLt4 || ieLt10 ) {
//		
//		$('#slideshow').remove();
//	} else { 
//		
//		$('#slider').remove();
//	}
	
	// Si IE < 10
	if(ieLt10) {
	  $(".news_corner").addClass("news_corner_old");
	  $(".bandeau_login").addClass("bandeau_login_old");
	  $(".header2").append("<div class='header2_corner_top'></div><div class='header2_corner_bottom'></div>");
	  $(".pwd_lost").prepend("<div class='pwd_lost_corner'><div class='pwd_lost_corner_top'></div><div class='pwd_lost_corner_bottom'></div>");
	  $(".pwd_lost").addClass("pwd_lost_old");
	  $(".pwd_lost_content").addClass("pwd_lost_content_old");
	}
	/*************************************************/

	/****************************************************************/
		
	/********************* Slideshow News *************************/
	$("#slider").easySlider({
		auto: true,
		continuous: true,
		speed:5000
	});
	/**************************************************************/
	/*
	$(".login input").keypress(function(event) {
	  if ( event.which == 13 ) {
	     event.preventDefault();
	     $(".access input").click();
	   }
	});
	*/	
	
	/****** Change arrow color on hover link 'not referenced'
	$('.buy a').hover(function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_right_hover.png');
	},function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_right.png');
	});
	******/
	/**************************************************************/
	
	/****** Change arrow color on hover link 'lost password' 
	$('#lostPassword').hover(function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_down_hover.png');
	},function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_down.png');
	});
	******/
	/**************************************************************/
	
	
	/**************************************************************/

	var diff = document.documentElement.clientHeight - $("body").height();
	//if (ie) diff -= 12;
	diff -=25;
	$(".footer").css("bottom",'-'+diff+'px');
});

