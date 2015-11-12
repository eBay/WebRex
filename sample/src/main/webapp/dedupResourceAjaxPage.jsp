<!DOCTYPE HTML>
<%@page import="com.ebayopensource.webrex.resource.ResourceRuntimeContext"%>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<res:bean id="res"></res:bean>
<!-- Set token in resource context  -->
<%
String token = request.getParameter("ajaxDedupToken");
ResourceRuntimeContext ctx = ResourceRuntimeContext.ctx();
ctx.setDeDupToken(token);

%>
<html>
<head>
  <!-- define css slot -->
  <res:cssSlot id="myCss"></res:cssSlot>
  <res:useCss>
     .cnt{
     	background-color:#FF9900;
     	color: #FFF;
     	padding: 20px;
     }
  </res:useCss>
  <res:useCss value="/css/table.css" target="head-css"></res:useCss>
</head>
<body>
  
  <!-- define js slot -->
  <res:jsSlot id="myBody"></res:jsSlot>
  <!-- refer sample1.js(local resource) -->
  <res:useJs value="/js/sample1.js" target="myBody"></res:useJs>
  <!-- refer sample2.js(local resource) -->
  <res:useJs value="/js/sample2.js" target="myBody"></res:useJs>
  <!-- refer commonSample1.js(shared resource) -->
  <res:useJs value="/js/commonSample1.js" target="myBody"></res:useJs>
  <!-- refer commonSample2.js(shared resource) -->
  <res:useJs value="/js/commonSample2.js" target="myBody"></res:useJs>

  <div class="cnt">
  Ajax response content...
  <br><br><i>(Tips: as you can see, the included JS don't print the same content with the main page)</i><br><br>
  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus convallis molestie erat, ut adipiscing risus blandit vel. Vivamus luctus elementum lorem, eu sodales velit sagittis id. Donec a est ligula, eget volutpat augue. Morbi ante nunc, venenatis vel mollis ut, viverra vehicula turpis. Etiam pulvinar dui dignissim sapien gravida ut congue libero ultrices. Mauris sapien sem, molestie sit amet aliquet ac, aliquet quis lorem. Cras ipsum lectus, tincidunt ac dictum in, imperdiet vitae est. Integer feugiat interdum dolor commodo dapibus. Etiam pellentesque mi eget quam malesuada vel suscipit ipsum ullamcorper. Suspendisse potenti.						
  </p>
  </div>
  
</body>
</html>