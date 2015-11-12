<%@ tag description="Creates buttons" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="res" uri="http://www.ebay.com/webrex/core"%>

<%@ attribute name="value" required="false"
	description="Text of the button"%>
<%@ attribute name="type" required="false"
	description="Input type: submit, button(default), anchor"%>
<%@ attribute name="disabled"
	description="Specify whether the button is disabled: true, false(default)"%>
<%@ attribute name="cssSlot" description="Target for CSS slot"%>
<%@ attribute name="jsSlot" description="Target for JS slot"%>
<%@ attribute name="id" description="Id of the button"%>
<%@ attribute name="title" description="Title of the button"%>


<c:set var="cssSlotVal" value="${(not empty cssSlot)? cssSlot : 'head'}" />
<res:useCss value="/button/button.css" target="${cssSlotVal}" />

<c:set var="sizeClass" value=" btn-m" />
<c:set var="colorClass" value=" btn-prim" />

<c:if test="${disabled eq 'true'}">
	<c:set var="disabledClass" value=" btn-d" />
	<c:set var="disabledAttr" value="disabled='disabled'"></c:set>
</c:if>

<c:if test="${not empty title }">
	<c:set var="titleAttr" value=" title='${title}'"></c:set>
</c:if>

<c:choose>
	<c:when test="${type eq 'submit'}">
		<input id="${cmpId}"
			class="btn${sizeClass} ${colorClass} ${disabledClass}" type="submit"
			${titleAttr } ${disabledAttr} value="${value}">
	</c:when>
	<c:when test="${type eq 'anchor'}">
		<c:if test="${empty url}">
			<c:set var="url" value="#" />
		</c:if>
		<c:choose>
			<c:when test="${disabled eq 'true'}">
				<a id="${cmpId}"
					class="btn${sizeClass} ${disabledClass} ${colorClass}"
					target="${target}">${value}</a>
			</c:when>
			<c:otherwise>
				<a id="${cmpId}" class="btn${sizeClass} ${colorClass}" href="${url}"
					target="${target}">${value}</a>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<button ${titleAttr }
			class="btn${sizeClass} ${colorClass} ${disabledClass}"
			${disabledAttr}>${value }</button>
	</c:otherwise>
</c:choose>
