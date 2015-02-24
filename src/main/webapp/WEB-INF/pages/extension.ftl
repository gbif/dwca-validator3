<#-- @ftlvariable name="" type="org.gbif.dwca.action.ExtensionsAction" -->
<#include "/WEB-INF/pages/inc/header.ftl">
	<title>${extension.title}</title>
<#assign currentMenu = "extensions" />
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>${extension.title}</h1>
<h1 class="small">${extension.rowType}</h1>
<#if extension.dev>
<p><img src="images/warning.gif"/> <em>Under Development</em></p>
</#if>
<p>${extension.description!}</p>
<#if extension.subject?has_content>
<p>Keywords: <em>${extension.subject}</em></p>
</#if>
<#if extension.link?has_content>
<p>Link: <a href="${extension.link}">${extension.link}</a></p>
</#if>
   
<p>
	<a href="extensions.do"><button>back</button></a>
	<a href="extensionTxt.do?id=${extension.rowType}"><button>download</button></a>
</p>
<br/>

<h1>Properties</h1>

<#assign group=""/>
<ul class="horizontal">
<#list extension.properties as p>
<#if (p.group!"")!="" && (p.group!"")!=group>
	<#assign group=p.group/>
	<li><a href="#${p.group?url}">${p.group}</a></li>
</#if>
</#list>
</ul>

<br/><br/>
                                   
<#assign group=""/>
<#list extension.properties as p>

<#if p.group?exists && p.group!=group>
	<#assign group=p.group/>
	<a name="${p.group?url}"></a>
	<h2>${p.group}</h2>
</#if>
	
<a name="${p.qualname}"></a>          
<div class="definition">	
  <div class="title">
  	<div class="head">
		${p.name}
  	</div>
  </div>
  <div class="body">
      	<div>
			${p.description!"No description available"}
			<#if p.description?has_content><br/></#if>              	
			<#if p.link?has_content>See also <a href="${p.link}">${p.link}</a></#if>              	
      	</div>
      	<#if p.examples??> 
      	<div>
          	<em>Examples</em>: 
			${p.examples}
      	</div>
      	</#if>
      	<div class="details">
      		<table>
          		<tr><th>Qualified Name</th><td>${p.qualname}</td></tr>
          		<tr><th>Namespace</th><td>${p.namespace}</td></tr>
          		<#if p.group??><tr><th>Group</th><td>${p.group}</td></tr></#if>
          		<tr><th>Data Type</th><td>
          			<#if p.vocabulary?exists>
          				<a href="vocabulary.do?id=${p.vocabulary.uri}">${p.vocabulary.title}</a>
			      	<#else>
			      		${p.type!"string"}
      				</#if>
          		</td></tr>
          		<tr><th>Required</th><td>${p.required?string}</td></tr>
      		</table>
      	</div>
  </div>
</div>
</#list>

<#include "/WEB-INF/pages/inc/footer.ftl">
