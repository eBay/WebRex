<!DOCTYPE HTML>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<res:bean id="res"></res:bean>
<html>
<head>
<title>ReferLocalResource</title>
<!-- define css slot -->
<res:cssSlot id="head-css"></res:cssSlot>

<res:useCss value="/css/table.css" target="head-css"></res:useCss>
<res:useCss value="/css/common.css" target="head-css"></res:useCss>

<!-- refer sample1.css(local resource) -->
<res:useCss value="/css/sample1.css" target="head-css"></res:useCss>
<!-- refer sample2.css(local resource) -->
<res:useCss value="/css/sample2.css" target="head-css"></res:useCss>

</head>
<body>
	<div class="header">
	<!-- refer image(local resource) -->
 	 <res:img value="/img/webrexLogoHeader.png" class="lgctr"/><h2>WebRex Example: refer local resources</h2>
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
			<tbody>
			<tr>
				<!-- refer image(local resource) -->
				<td>Example for referring an image from local web project:</td>
				<td>&lt;res:img value="/img/webrexLogo.png" /&gt;</td>
				<td><res:img value="/img/webrexLogoMini.png"/></td>
			</tr>
			<tr>
				<!-- define js slot -->
				<td>Define a JS slot body-js:</td>
				<td>&lt;res:jsSlot id="body-js"/&gt;</td>
				<td><res:jsSlot id="body-js" /></td>
			</tr>
			<tr >
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
			</tbody>
		</table>
	</div>

</body>
</html>