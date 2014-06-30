<#include "/WEB-INF/pages/inc/header.ftl">
	<title>EML Validation Result</title>
	<style>
h1 {
	margin-bottom: 0px;
}
h1 > h2 {
	margin-top: 10px;
}
	</style>
<#assign currentMenu = "eml" />
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>EML Validation Result</h1>

<#if gbifSchemaValidated>
<p class="good">Eml document validated according to <a href="${schemaEmlGbifUrl}">GBIF EML Profile</a>.</p>
	<#if emlSchemaValidated>
	<p class="good">Eml document validated according to official <a href="${schemaEmlUrl}">EML 2.1.1</a> xml schema.</p>
	<#else>
	<p class="warn">Failed validation against the official <a href="${schemaEmlUrl}">EML 2.1.1</a> xml schema.</p>
	</#if>
<#else>
<p class="warn">Failed validation against the <a href="${schemaEmlGbifUrl}">GBIF EML Profile</a>.</p>
</#if>

<#if eml??>
<p class="good">Eml document parsed successfully with dwca reader!</p>
<div class="details">
<table>
	<tr><th>Title</th><td>${eml.title!}</td></tr>
	<tr><th>Description</th><td>${eml.description!}</td></tr>
	<tr><th>Subject</th><td>${eml.subject!}</td></tr>
	<tr><th>HomeUrl</th><td>${eml.homeUrl!}</td></tr>
	<tr><th>LogoUrl</th><td>${eml.logoUrl!}</td></tr>
	<tr><th>Published</th><td>${eml.published?date?string.medium!}</td></tr>
</table>
</div>
<#else>
<p class="warn">Failed parsing the eml document with the dwca reader.</p>
</#if>

<hr/>

<#if metadataException?exists>
	<p class="warn">Eml validation error</p>
	<pre>${metadataException}</pre>
<pre>
<#list dwcaStackTrace as el>
${el}	
</#list>
</pre>
</#if>

<#include "/WEB-INF/pages/inc/footer.ftl">
