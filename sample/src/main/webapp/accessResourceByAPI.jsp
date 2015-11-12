<!DOCTYPE HTML>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>
<%@page import="com.ebayopensource.webrex.resource.ResourceRuntimeContext"%>
<%@page import="com.ebayopensource.webrex.resource.ResourceFactory"%>
<%@page import="com.ebayopensource.webrex.resource.ResourceAggregator"%>
<%@page import="com.ebayopensource.webrex.resource.api.IResource"%>
<%@page import="java.util.*"%>
<res:bean id="res"></res:bean>
<%
//Create resource
IResource resource = ResourceFactory.createResource("/js/sample1.js");
 
//Try to get resource url with current context
String url = resource.getUrl(ResourceRuntimeContext.ctx().getResourceContext());


List<IResource> iResources = new ArrayList<IResource>(); 
//Create resource
iResources.add(ResourceFactory.createResource("/js/sample1.js"));
iResources.add(ResourceFactory.createResource("/js/sample2.js"));
 
//Try to get aggregation resource url with current context
String aggUrl = ResourceFactory.createAggregatedResource(iResources).getUrl(ResourceRuntimeContext.ctx().getResourceContext());

//Get sample1.js
IResource sampleResource = ResourceFactory.createResource("/js/sample1.js");

//Register resource in "body-js" slot
ResourceAggregator aggregator = ResourceRuntimeContext.ctx().getResourceAggregator();
aggregator.registerResource("body-js", sampleResource);		

%>
<html>
<head>
<title>access and manage resource by JAVA API</title>
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
 	 <res:img value="/img/webrexLogoHeader.png" class="lgctr"/><h2>WebRex Example: access and manage resource by JAVA API</h2>
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
				<td>Get the url for a single resource:</td>
				<td><pre><span class="jcom">// Create resource</span>
IResource resource = ResourceFactory.createResource("/js/sample1.js");
	 
<span class="jcom">// Try to get resource url with current context</span>
String url = resource.getUrl(ResourceRuntimeContext.ctx().getResourceContext());
</pre>					
				</td>
				<td><%=url %></td>
			</tr>
			<tr>
				<td>Get the url for an aggregation result:</td>
				<td><pre>
List&lt;IResource&gt; iResources = new ArrayList&lt;IResource&gt;();  

<span class="jcom">//Create resource</span>
iResources.add(ResourceFactory.createResource("/js/sample1.js"));
iResources.add(ResourceFactory.createResource("/js/sample2.js"));

<span class="jcom">//Try to get aggregation resource url with current context</span>
String aggUrl = ResourceFactory.createAggregatedResource(iResources).getUrl(
				ResourceRuntimeContext.ctx().getResourceContext());
</pre></td>
				<td><%=aggUrl %></td>
			</tr>
			<tr >
				<td>Add resources into an existing slot:</td>
				<td><pre>
<span class="jcom">// Get sample1.js</span>
IResource sampleResource = ResourceFactory.createResource("/js/sample1.js");

<span class="jcom">// Register resource in "body-js" slot</span>
ResourceAggregator aggregator = ResourceRuntimeContext.ctx().getResourceAggregator();
aggregator.registerResource("body-js", sampleResource);				
</pre></td>
				<td><res:jsSlot id="body-js"/></td>
			</tr>
			
			</tbody>
		</table>
	</div>


</body>
</html>