function str(x) {
	if(x!=null && x.length > 0) return x;
	return '';
}
function isTrueOrNull(x){
	if (x==null || x==true){
		return true;
	}
	return false;
}
function initHelp(){
    $(".infoImg").click(function(e) {
        var show = $(this).next().is(":hidden");
	    $("div.info:visible").hide("fast");
	    if (show){
	        $(this).next().show("fast");
	    };
    })	
	$("div.info").click(function(e) {
        $(this).hide("fast");
    })
    $(document).keyup(function(e) {
    	// pressing the escape key
  		if (e.keyCode == 27) { 
	        $("div.info:visible").hide("fast");
  		}   
	});
    $("div.info ol a").click(function(e) {
    	e.preventDefault(); 
        $(this).parent().parent().parent().next().val($(this).attr("val"));
        $(this).parent().parent().parent().hide("fast");
    })
}
