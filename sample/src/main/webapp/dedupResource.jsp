<!DOCTYPE HTML>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<res:bean id="res"></res:bean>
<html>
<head>
<title>DedupResourceExample</title>
  <!-- define css slot -->
  <res:cssSlot id="myCss"></res:cssSlot>
  
  <res:useCss value="/css/table.css" target="head-css"></res:useCss>
  <res:useCss value="/css/common.css" target="head-css"></res:useCss>
  
  <!-- refer sample1.css(local resource) -->
  <res:useCss value="/css/sample1.css" target="myCss"></res:useCss>
  <!-- refer sample2.css(local resource) -->
  <res:useCss value="/css/sample2.css" target="myCss"></res:useCss>
  <!-- refer commonSample1.css(shared resource) -->
  <res:useCss value="/css/commonSample1.css" target="myCss"></res:useCss>
  <!-- refer commonSample2.css(shared resource) -->
  <res:useCss value="/css/commonSample2.css" target="myCss"></res:useCss>
  
    <res:useCss value="/css/app.css" target="myCss"></res:useCss>
    
   <res:jsSlot id="head-js"></res:jsSlot>
   <res:useJs value="/js/jquery-1.7.2.js" target="head-js"></res:useJs>
   
</head>
<body>	
	<div class="header">
	<!-- refer image(local resource) -->
 	 <res:img value="/img/webrexLogoHeader.png" class="lgctr"/><h2>WebRex Example: dedup resources in ajax requests</h2>
	</div>

	<a href="index.jsp">Go back to homepage</a>
	<br>
	<br>
	
	<div class="wrapper">
	  <input id="btnAjax" type="button" value="click me!"/>
	  	<br>
		<br>
	  <!-- Generate a unique dedup "token" on the main page. -->
	  <input type="hidden" id="ajaxDedupToken" value='<res:token type="js"/>' />
	  
	  <div id="ajaxResp" class="container">Container for ajax response</div>  	
	  <br>
	  
	  <!-- define js slot -->
	  <res:jsSlot id="myBody"></res:jsSlot>
	  
	  <!-- Append the generated token as an Ajax request parameter. -->
	  <res:useJs value="/js/dedupResourcesUtil.js" target="myBody"></res:useJs>
	   
	  <!-- refer sample1.js(local resource) -->
	  <res:useJs value="/js/sample1.js" target="myBody"></res:useJs>
	  <!-- refer sample2.js(local resource) -->
	  <res:useJs value="/js/sample2.js" target="myBody"></res:useJs>
	  <!-- refer commonSample1.js(shared resource) -->
	  <res:useJs value="/js/commonSample1.js" target="myBody"></res:useJs>
	  <!-- refer commonSample2.js(shared resource) -->
	  <res:useJs value="/js/commonSample2.js" target="myBody"></res:useJs>
	  
	  
	  
	  
	  
	  <br><br>
	  <div class="extl">General Information</div>
	  <hr role="separator" class="fdhr">
	  <div class="intro">Background</div>
		<div class="bgcnt">
			By default before sending response WebRex remove the duplicated resources on the same page to reduce the page weight and speed up the page loading.
			Such automatic dedup feature only works for the resources on the same page. However more and more pages have Ajax requests, these pages might include JS/CSS resources via Ajax requests. Sometimes these resources loaded by Ajax requests will cause issues. For example, on the main JSP page, a page component is initialized by executing JS in a JS file included in the page, then an Ajax request downloads this initialization JS file again,  so that the initialization code is executed twice, which cause an error on the page.
			To resolve such kind of issues, we need to remove the duplicated resources loaded by the main page and the Ajax request.
		</div>
		
		<div class="intro">General Dedup logic and steps</div>
		<div class="bgcnt">
			The following are the general steps about how to do Ajax dedup by application developer:<br><br>
		
		    1. <b>Generate a unique dedup "token" on the main page.</b><br>
		    WebRex provide resource tag&lt;res:token&gt; to generate the token for the page. The content of the token is calculated from all the resources that are used on the page. WebRex can use this token to track down the resources that are already included.<br><br>
		     
		    2. <b>Append the generated token as an Ajax request parameter.</b><br><br>
		     
		    3. <b>Set token in resource context.</b><br>
		    In server side, get the token from Ajax request and set the token value to resource context before any resources requests are handled. Then WebRex can detect which resources should be deduped based on the information per the token. Application developers can read this parameter in Spring Controllers or pipeline handlers, etc.<br><br>
		
		</div> 
	  
   </div>


</body>
</html>