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

	/****** Open / Close div 'lost password' *********/
//	$("#lostPassword").click(function(e){
//		$('.email').val('');
//		$(".pwd_lost").slideToggle('fast',function(){
//			$('.email').focus();
//		});
//		return false;
//	});
	/*************************************************/

	/****** Close div 'lost password' & 'lang' on body clic *********/
//	$("body").click(function(e){
//		var classe = $(e.target).attr("class");
//		if (classe == undefined) classe = '';
//
//		if (classe.indexOf("pwd_lost") == -1 && classe.indexOf("email") == -1 && classe.indexOf("watermark") == -1  && $(".pwd_lost").css('display') != 'none') {
//			$(".pwd_lost").slideUp('fast');
//		}
//		if ($(e.target).attr("id") != "lang" && classe != "langues" && $("span[class='langues']").css('display') != 'none') {
//			$("span[class='langues']").slideUp('fast');
//		}
//	});

	/****************************************************************/

	/***************** Compressible services list *******************/
	$("ul.list > li > a").click(function(e,i){
		if ($(e.target).next().css('display') == 'none' && $("ul.list  > li > div:visible").length != 0)
		{
			var src = $("ul.list > li > div:visible").prev().children('input').attr('src');
			if (src.indexOf('up') != -1) {
				$("ul.list > li > div:visible").prev().children('input').attr('src','resources/brand/images/down.png');
			} else {
				$("ul.list > li > div:visible").prev().children('input').attr('src','resources/brand/images/up.png');
			}
			$("ul.list > li > div:visible").slideToggle('fast');
		}

		var src = $(e.target).children('input').attr('src');
		if (src.indexOf('up') != -1) {
			$(e.target).children('input').attr('src','resources/brand/images/down_hover.png');
		} else {
			$(e.target).children('input').attr('src','resources/brand/images/up_hover.png');
		}
		$(e.target).next().slideToggle('fast');
		
		return false;
	});
	
	$("ul.list > li > a > input").click(function(){ return false; });
	/**************************************************************/

		
	/********************* Slideshow News *************************/
	$("#slider").easySlider({
		auto: true,
		continuous: true,
		speed:5000
	});
	/**************************************************************/
	
	$(".login input").keypress(function(event) {
	  if ( event.which == 13 ) {
	     event.preventDefault();
	     $(".access input").click();
	   }
	});
	
	
	
	/********************** Language bar **************************/
	$('#lang').mouseover(function(e) { 
		if ($("span[class='langues']").css('display') == 'none') {
			$("span[class='langues']").slideToggle('fast');
		}
	});
	$(".langues img").click(function(e,i){
		var src1 = $("#lang").attr('src');
		var src2 = $(e.target).attr('src');
		$("#lang").attr('src',src2);
		$(e.target).attr('src',src1);
		$("span[class='langues']").slideToggle('fast');
	});
	/**************************************************************/
	
	/************ Fix watermark for password field ****************/
	$('.pwd_lost .watermark').css('height',20);
	$('.pwd_lost .watermark').css('line-height','20px');
	/**************************************************************/
	
	/****** Change arrow color on hover link 'not referenced'******/
	$('.buy a').hover(function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_right_hover.png');
	},function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_right.png');
	});
	/**************************************************************/
	
	/****** Change arrow color on hover link 'lost password' ******/
	$('#lostPassword').hover(function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_down_hover.png');
	},function(){
		$(this).children("input").attr('src','resources/brand/images/arrow_down.png');
	});
	/**************************************************************/
	
	/********* Change arrow color on hover link 'service **********/
	$('.service a').hover(function(){
		var src = $(this).children("input").attr('src');
		src = src.replace('.png', '_hover.png');
		$(this).children("input").attr('src',src);
	},function(){
		var src = $(this).children("input").attr('src');
		src = src.replace('_hover', '');
		$(this).children("input").attr('src',src);
	});
	/**************************************************************/
	
	/************** Google search on steeluser.com ****************/
//	$("#searchButton").click(function(e,i){
//		var search = $("#inputSearch").val();
//		if (search != '') {
//			location.href = "http://www.google.fr/#q=" + search + "+site:www.steeluser.com";
//		}
//	});
	/**************************************************************/

	var diff = document.documentElement.clientHeight - $("body").height();
	//if (ie) diff -= 12;
	diff -=25;
	$(".footer").css("bottom",'-'+diff+'px');
});