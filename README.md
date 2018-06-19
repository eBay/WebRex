![eBay Open Source ](https://github.com/eBay/WebRex/raw/master/sample/src/main/webapp/resources/img/webrexLogoHeader.png)WebRex [![Build Status](https://api.travis-ci.org/eBay/WebRex.png?branch=master)](https://travis-ci.org/eBay/WebRex)
==================


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

# Table of Contents


- [Project overview](#project-overview)
- [Concepts](#concepts)
  - [Resource](#resource)
  - [Aggregation](#aggregation)
  - [Slot](#slot)
  - [Resource root path](#resource-root-path)
  - [Resource pre-processing](#resource-pre-processing)
- [How it works](#how-it-works)
- [Prerequiste](#prerequiste)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
  - [Include the WebRex tag lib](#include-the-webrex-tag-lib)
  - [Define a slot in the JSP page](#define-a-slot-in-the-jsp-page)
  - [Assign resources to the slots](#assign-resources-to-the-slots)
  - [Usage of other resource types](#usage-of-other-resource-types)
- [WebRex Java API](#webrex-java-api)
  - [Get the url for a single resource](#get-the-url-for-a-single-resource)
  - [Get the url for an aggregation result](#get-the-url-for-an-aggregation-result)
  - [Add resources into an existing slot on the page](#add-resources-into-an-existing-slot-on-the-page)
- [FAQ](#faq)
  - [Why do I get an exception: java.lang.IllegalStateException: ResourceRuntimeContext must be setup by ResourceRuntimeContext.setup! when hitting the page or calling the Java API?](#why-do-i-get-an-exception-javalangillegalstateexception-resourceruntimecontext-must-be-setup-by-resourceruntimecontextsetup-when-hitting-the-page-or-calling-the-java-api)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Project overview 
For big web projects, usually we need to do optimization for web resources like JS and CSS files. For example, we might need to aggregate multiple small CSS files into one big file to reduce the HTTP requests sent out by the client browser. There are many open source solutions to deal with the resource aggregation, like Google Wro4j and JAWR, but most of these open source solutions are "static" aggregation solutions. 

With static aggregation, the aggregation rules are defined in a separate configuration file(or other configuration system), and the aggregations are generated according to this configuration despite how the resources are actually used. But sometimes the usage of these resources might be very dynamic. For example, sometimes different resources will be used for different runtime conditions(user browser agent, user locale, etc), in this case, it might be hard to create configurations for all these runtime conditions.

WebRex is such a tool which will dynamically aggregate resources together duing runtime. It provides the following features:
* A set of resource JSP tags to collect resource usage information on the JSP page, so that no aggregation configuration file is needed.
* Resource scan and load during runtime, which can load resources from dependent jar files.
* A resource servlet which will serve the aggregated resources during runtime.
* Built in CSS processors which will process the background images in the CSS files.
* A set of Java API which can be extended to support other features like integration of CDN network, resource minification and obfuscation, etc.

# Concepts
## Resource
WebRex defines resources as the files or fragments that are not used to generate the HTML dom, but these files or fragments are parts of the output HTML that will help to render the page. The most commonly used resources are CSS file, JS files and images. Some of the resources can be static, like a file on the disk, others can be dynamic. They can be code generated fragments in the JSP file.

## Aggregation
An HTML page may contain multiple CSS or JS resources. The browser will send out multiple HTTP requests to fetch these resources. But if we can combine the content of the CSS resources or the JS resources into one bigger resource, then fewer HTTP request roundstrips are required, but the page look and feel remains the same. The process of combining the CSS or JS resources is called resource aggregation.

## Slot
In WebRex, resources aggregation is achieved by defining slots on the JSP page and assigning resources to the slots. A slot is a JSP tag, which is a place holder on the JSP that defines the position where the aggregated resources will go. Slot has an ID so that resources can be assigned to a slot when referring the resource. Slot also has other attributes to control the output of the slot tag.

## Resource root path
When you are using the WebRex JSP tags to assign a resource to a slot, you can use EL(expression language) or relative path to refer to the resource. WebRex will look up these resources on the disk(resource resolving). According to the convention, WebRex will look up several predefined folders to find the resources. For those resources from the WAR project(local resources), it will search the folder <WAR_ROOT>/resources/ folder. For those resources from a dependent JAR file(shared resources), it will search the folder META-INF/resources folder in the JAR file.

## Resource pre-processing
Some resources like CSS files will refer to other resources, like background images in CSS files. These background image urls need to be modified to the WebRex format. In WebRex, you can apply a pre-processor to a type of resources when this type of resources is being loaded. In WebRex, a built in CSS pre-processor is applied to all CSS resources. This pre-processor will scan the CSS content and replace image background paths with the proccessed urls.

# How it works
1. When a JSP page is executed, WebRex will construct a resource usage model for the current page, according to the resource tags defined on the page.
2. A resource marker like ${MARKER:css} will be generated for each resource tag and slot tag.
3. After the page is all rendered, the resource usage model will be completed, then different aggregations will be created for different slots according to how many resources are actually used and assigned to slot.
4. A resource RequestFilter will process the HTTP response after the JSP is executed, scanning all the markers in the result HTML and replace them with the final aggregation results.

# Prerequiste
* JDK 7.0 and above
* J2EE container which supports Servlet 3.0 spec, like Tomcat 7.0.42 and above.

# Installation

# Configuration
WebRex will scan the resource root folders for local resources. By default all resources are loaded from &lt;WAR_ROOT&gt;/resources/ folder. The resource root folders can be configured through resources.properties file under <WAR_ROOT>/WEB-INF/webrex folder. Here is a configuration example:
	&#0023; These properties are set as resource root for different resource type.
	&#0023; By default, for local web resources(js, css, image), the resources are looked up from below directory under src/main/webapp.
	&#0023; If users want to put the resource in other folders, please uncomment and update the related property.
	
	image.base=/resources
	css.base=/resources
	js.base=/resources
	flash.base=/resources
	common.base=/resources

The resource root folders for shared resources can't be configured. All shared resources are loaded from &lt;jar_file_path&gt;!/META-INF/resources folder by convention.

# Usage
## Include the WebRex tag lib
	<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>

## Define a slot in the JSP page
	<res:cssSlot id="myCss" />
	<res:jsSlot id="myBody" />

## Assign resources to the slots
	<res:useCss value="/css/sample1.css" target="myCss" />
	<res:useJs value="/js/sample1.js" target="myBody" />
	<res:useJs target="myBody">
		document.write("This is a sample page");
	</res:useJs>

## Usage of other resource types
	<res:img value="/sample/webrex.jpg"/>
	<res:flash value="/flash/video.swf"/>
	<res:resource value="/font/Arial.woff"/>

# WebRex Java API
## Get the url for a single resource
	// Create resource
	IResource resource = ResourceFactory.createResource("/bitbybit/BitByBit.js");
	
	// Try to get resource url with current context
	String url = resource.getUrl(ResourceRuntimeContext.ctx().getResourceContext());

## Get the url for an aggregation result
	List<IResource> iResources = new ArrayList<IResource>();  
	
	//Create resource
	iResources.add(ResourceFactory.createResource("/tests/jsTest_sample3.js"));
	iResources.add(ResourceFactory.createResource("/js/test1.js"));
	
	//Try to get aggregation resource url with current context
	String url = ResourceFactory.createAggregatedResource(iResources).getUrl(ResourceRuntimeContext.ctx().getResourceContext());

## Add resources into an existing slot on the page
	// Get Sample1 JS
	IResource resource = ResourceFactory.createResource("/Sample1.js");
	
	// Register resource in "page-js" slot
	ResourceAggregator aggregator = ResourceRuntimeContext.ctx().getResourceAggregator();
	aggregator.registerResource("page-js", resource);

# FAQ
## Why do I get an exception: java.lang.IllegalStateException: ResourceRuntimeContext must be setup by ResourceRuntimeContext.setup! when hitting the page or calling the Java API?
A: WebRex runs in a multithread environment. For every user request, a threadlocal resource context, which is carrying essential user context, needs to be initialized for every thread. This job is done by the ResourceFilter automatically for every HTTP request. If you see the above exception, it means that the current thread is not set up correctly. Please check if the filter is correctly configured and is working correctly.
## When there are multiple inline CSS snippets, why aren't the CSS requests aggregated into one request?
A: The root cause is, inline CSS snippets are alternate with externalized CSS, for example,

	<res:cssSlot id="head-css"></res:cssSlot>
	<res:useCss value="/css/common.css" target="head-css"></res:useCss>
	<res:useCss target="head-css">
		body {
			background-color: #ffd;
		}	
	</res:useCss>
	...
	<res:useCss value="/css/table.css" target="head-css"></res:useCss>
	<res:useCss target="head-css">
		.logo {
			color: #c1c1c1;
		}	
	</res:useCss>
	
The output of above code is 
     
	<link href="/sample/lrssvr/45ztgzb02q5djc4iivuiuwbgci0.css" type="text/css" rel="stylesheet">
	<style type="text/css">
		body {
			background-color: #ffd;
		}	
	</style><link href="/sample/lrssvr/yupwlhwnme40djtrofgdbhnszqe.css" type="text/css" rel="stylesheet">
	<style type="text/css">
		.logo {
			color: #c1c1c1;
		}	
	</style>
      
And if we change code as the following,
      
	<res:useCss value="/css/common.css" target="head-css"></res:useCss>
	<res:useCss value="/css/table.css" target="head-css"></res:useCss>
	<res:useCss target="head-css">
		body {
			background-color: #ffd;
		}	
	</res:useCss>
	...
	<res:useCss target="head-css">
		.logo {
			color: #c1c1c1;
		}	
	</res:useCss>
	
The output would be 
	
	<link href="/sample/lrssvr/vxrummlit24jlp3abm4ecr1ajic.css" type="text/css" rel="stylesheet">
	<style type="text/css">
	body {
		background-color: #ffd;
	}	

	.logo {
		color: #c1c1c1;
	}	
	</style>


