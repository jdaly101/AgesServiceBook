var elements;
var dayBackgroundColor;
var dayFontColor;
var dayMenuIconColor;
var dayMenuBarColor;
var redElements;


function swapLang(myRow) {
	$(myRow.cells).toggle();
}

function stopSwap(myRow) {
	$("tr:has(.media-group)").removeAttr("onclick","swapLang(this)");
}

function resumeSwap(myRow) {
    $("tr:has(.media-group)").attr("onclick","swapLang(this)");
}


//This function is handled by pref.generation.ares
//$(document).ready(function(){
//	$("td.leftCell span.versiondesignation").css("display","none");
//	});


$.expr[':'].notext = function detectNoText(x){ return x.innerHTML && x.innerHTML.replace(/(<!--.*(?!-->))|\s+/g, '').length === 0 }

function notAvailable() {
	$('p.hymn:has(span.dummy)').removeClass("hymn").addClass("notavailable").text("This text was inaccessible at the time of publication or unavailable due to copyright restrictions.").css("background-color","white");
	}

//This version of the script displays the versiondesgination
//function notAvailable() {
//		$('p.hymn > span.dummy').removeClass("hymn").addClass("notavailable").text("This text was //inaccessible at the time of publication or unavailable due to copyright //restrictions.").css("background-color","white");
//		}

$.expr[':'].noValue = function detectNoValue(x){
	if ($(x).find("div.media-group").length > 0) {
	  return false;
	} else if ($(x).text().trim().length === 0) {
	   return true;
	} else {
	  return false;
	}
};

function hideEmptyRows() {
	$("tr:noValue").css("display","none");
}


$(window).bind("load", function() {
    console.log("load binding");
	$("span.media-icon").attr("title","Open Lang 2 Western");
	$("span.media-icon-audio").attr("title","Open Lang 2 Audio");
 	$('.content').css('top', parseInt($('.navbar').css("height"))+10);
 	$('#accordion').on('show.bs.collapse', function () {
    if (active) $('#accordion .in').collapse('hide');
	});
	$('body').on('touchstart.dropdown', '.dropdown-menu', function (e) {
	    e.stopPropagation();
	});
});

function initDropdown() {

}

$(document).ready(function(){
//	$('.collapse').collapse()
//	dayBackgroundColor = $("body").css('background-color');
//	dayFontColor = $("body").css('color');
//	dayMenuIconColor = $("i.ages-menu-link").css('color');
//	dayMenuBarColor  = $("div.agesMenu").css('background-color');
//	redElements = $('*').filter(function(){ return ( $(this).css('color') == "rgb(255, 0, 0)");  });

//	if (getLanguages()) {
//        setLangVars();
//    }
//    console.log("stopping propagation");
//    $('.media-group > a').click(function (e) {
//        console.log("event", e);
//        //e.stopPropagation();
//        e.stopPropagation();
//        e.preventDefault();
//        console.log("buzz");
//    });

    elements = $(".content");

    notAvailable();
    hideEmptyRows();

 });
