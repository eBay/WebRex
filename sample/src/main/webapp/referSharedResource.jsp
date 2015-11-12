<!DOCTYPE HTML>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<%@ taglib prefix="sample" uri="http://ebayopensource.com/components"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<res:bean id="res"></res:bean>
<html>
<head>
<title>ReferSharedResource</title>
<!-- define css slot -->
<res:cssSlot id="page-css"></res:cssSlot>

<res:useCss value="/css/table.css" target="head-css"></res:useCss>
<res:useCss value="/css/common.css" target="head-css"></res:useCss>

<!-- refer sample1.css(local resource) -->
<res:useCss value="/css/sample1.css" target="page-css"></res:useCss>
<!-- refer sample2.css(local resource) -->
<res:useCss value="/css/sample2.css" target="page-css"></res:useCss>
<!-- refer commonSample1.css(shared resource) -->
<res:useCss value="/css/commonSample1.css" target="page-css"></res:useCss>
<!-- refer commonSample2.css(shared resource) -->
<res:useCss value="/css/commonSample2.css" target="page-css"></res:useCss>
</head>
<body>
	<div class="header">
	<!-- refer image(local resource) -->
 	 <res:img value="/img/webrexLogoHeader.png" class="lgctr"/><h2>WebRex Example: refer shared resources</h2>
	</div>

	<a href="index.jsp">Go back to homepage</a>
	<br>
	<br>
	<div class="rextbl">
	<table>
		<thead>
			<tr>
				<td>Feature</td>
				<td>Code Example</td>
				<td>Output</td>
			</tr>
		</thead>
		<tr>
			<!-- refer image(local resource) -->
			<td>Example for referring an image from dependency:</td>
			<td>&lt;res:img value="/img/example.png" /&gt;</td>
			<td><res:img value="/img/example.png" /></td>
		</tr>
		<tr>
			<!-- define js slot -->
			<td>Define a JS slot body-js:</td>
			<td>&lt;res:jsSlot id="body-js"/&gt;</td>
			<td><res:jsSlot id="body-js" /></td>
		</tr>
		<tr>
			<!-- refer sample1.js(local resource) -->
			<td>Add sample1.js from local web project to slot body-js:</td>
			<td>&lt;res:useJs value="/js/sample1.js" target="body-js"/&gt;</td>
			<td><res:useJs value="/js/sample1.js" target="body-js" /></td>
		</tr>
		<tr>
			<!-- refer sample2.js(local resource) -->
			<td>Add sample1.js from local web project to slot body-js:</td>
			<td>&lt;res:useJs value="/js/sample2.js" target="body-js"/&gt;</td>
			<td><res:useJs value="/js/sample2.js" target="body-js" /></td>
		</tr>
		<tr>
			<!-- refer commonSample1.js(shared resource) -->
			<td>Add commonSample1.js from a dependent Jar file to slot body-js:</td>
			<td>&lt;res:useJs value="/js/commonSample1.js" target="body-js"/&gt;</td>
			<td><res:useJs value="/js/commonSample1.js" target="body-js" /></td>
		</tr>
		<tr>
			<!-- refer commonSample2.js(shared resource) -->
			<td>Add commonSample2.js from a dependent Jar file to slot body-js:</td>
			<td>&lt;res:useJs value="/js/commonSample2.js" target="body-js"/&gt;</td>
			<td><res:useJs value="/js/commonSample2.js" target="body-js"></res:useJs></td>
		</tr>
		<tr>

			<td>Example for a shared component, all CSS resources are
				aggregated to the page-css slot:</td>
			<td>&lt;sample:button cssSlot="page-css" value="click me"/&gt;</td>
			<td><sample:button cssSlot="page-css" value="submit" /></td>
		</tr>
	</table>
	</div>
</body>
</html>