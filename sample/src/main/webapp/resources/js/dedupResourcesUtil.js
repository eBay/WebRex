!function ($) {
	$("#btnAjax").on("click", function (e) {
		var token = $("#ajaxDedupToken").val();
		var ajaxUrl = "dedupResourceAjaxPage.jsp";
		//var requestUrl= ajaxUrl+"?ajaxDedupToken="+token;	
		
		$.ajax({
			url: ajaxUrl,
			data: "ajaxDedupToken=" + token,
			cache:false,
			success: function(resp){
	             $("#ajaxResp").append(resp);
	             
			}
	    });
	});
}(window.jQuery);