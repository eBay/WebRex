<!DOCTYPE HTML>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<res:bean id="res"></res:bean>
<html>
<head>
  <title>Homepage</title>
  <!-- define css slot -->
  <res:cssSlot id="myCss"></res:cssSlot>
  <!-- refer sample1.css(local resource) -->
  <res:useCss value="/css/sample1.css" target="myCss"></res:useCss>
  <!-- refer sample2.css(local resource) -->
  <res:useCss value="/css/sample2.css" target="myCss"></res:useCss>
  
  <res:useCss value="/css/app.css" ></res:useCss>
</head>
<body>
  
  <div class="logo">
    <!-- refer image(local resource) -->
  	<res:img value="/img/webrexLogo.png"/>
  	<h2 class="tl">Welcome to eBay Open Source Resources Optimizer WebRex!</h2>
  </div>
    
  <div class="intro">This page demonstrates how to use WebRex to optimize your Web Resources during web application development.
 Web Resources include CSS/JS/Image/Flash, etc.
 <br><br>The following image resume the main purpose why web application developers should use WebRex.
 <br>
 <res:img value="/img/webrexAggr.png"/>
  </div>
  <div id="lst" >
   <div class="extl">Browse the following guides</div>
   <ol>
      <li><p><em><a href="referInlineResource.jsp">How to refer Inline Resource</a></em> Inline Resource means inline JS and CSS.</p></li>
      <li><p><em><a href="referLocalResource.jsp">How to refer Local Resource</a></em> Local Resource means resources in local war, and by default all resources are loaded from <b>&lt;WAR_ROOT&gt;/resources/</b> folder</p></li>
      <li><p><em><a href="referSharedResource.jsp">How to refer Shared Resource</a></em> Shared Resource means resources in jar dependencies. All shared resources are loaded from <b>&lt;jar_file_path&gt;!/META-INF/resources</b> folder by convention</p></li>
      <li><p><em><a href="dedupResource.jsp" >How to dedup resources in ajax response</a></em>Dedup means removing duplicated resources in ajax response if some specified resources are also referred in main page.</p></li>
      <li><p><em><a href="accessResourceByAPI.jsp">How to access and manage resource by JAVA API</a></em>
       WebRex helps developers easily access and manage the web resources in web project,
	   and it provides a set of JSP tag libraries as well as java API to manage resources. 
</p></li>
   </ol>
</div>
</body>
</html>
