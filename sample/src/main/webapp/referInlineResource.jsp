<!DOCTYPE HTML>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<res:bean id="res"></res:bean>
<html>
<head>
<title>ReferInlineResource</title>
	<!-- define css slot -->
	<res:cssSlot id="head-css"></res:cssSlot>
	<res:useCss value="/css/common.css" target="head-css"></res:useCss>
	<res:useCss>
	 body {
		background-color: #ffd;
		background-image: url('/img/example.png');
		background-repeat: no-repeat;
		background-position: right top;
		font-family: Arial, Sans-serif;
		margin: 0px;
		}
	  .cnt{
	    margin:auto;
	    width: 200px;
	  }		
	</res:useCss>

</head>
<body>
	<div class="header">
	<!-- refer image(local resource) -->
 	 <res:img value="/img/webrexLogoHeader.png" class="lgctr"/><h2>WebRex Example: refer inline resources</h2>
	</div>

	<a href="index.jsp">Go back to homepage</a>
	<br>
	<br>
	
  <div class="cnt">
    <res:useJs>
     for(var i=0; i<6; i++){
      document.write('this is inline JS<br>');
     }
  </res:useJs>
  </div> 

 
</body>
</html>