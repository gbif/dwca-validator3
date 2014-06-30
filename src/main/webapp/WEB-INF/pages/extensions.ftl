<#include "/WEB-INF/pages/inc/header.ftl">
	<title>Registered Extensions</title>
<#assign currentMenu = "extensions" />
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>Registered Extensions</h1>
<p>The following extensions are registered with GBIF - either for production or development purposes. <br/>
The list was last updated at <strong>${lastUpdatedExtensions?datetime?string.medium}</strong>.</br>
If you think we are missing an important extension please <a href="mailto:mdoering@gbif.org">contact us</a>.</p>

<h2>Stable Releases</h2>
<p>The extensions listed as stable releases are immutable and taken from the GBIF live registry.
Any changes needed will result in a new version with a new row type and namespace, leaving the existing one as it was.
</p>

<#list extensions as ext>	
<a name="${ext.rowType}"></a>          
<div class="definition">	
  <div class="title">
  	<div class="head">
        <a href="extension.do?id=${ext.rowType}">${ext.title}</a>
  	</div>
  </div>
  <div class="body">
      	<div>
			${ext.description!}
			<#if ext.link?has_content><br/>See also <a href="${ext.link}">${ext.link}</a></#if>              	
      	</div>
      	<div class="details">
      		<table>
          		<tr><th>Properties</th><td>${ext.properties?size}</td></tr>
          		<tr><th>Name</th><td>${ext.name}</td></tr>
          		<tr><th>Namespace</th><td>${ext.namespace}</td></tr>
          		<tr><th>RowType</th><td>${ext.rowType}</td></tr>
          		<tr><th>Keywords</th><td>${ext.subject!}</td></tr>
      		</table>
      	</div>
  </div>
</div>
</#list>


<h2>Under Development</h2>
<p>The extensions listed are taken from the sandbox environment and listed in the GBIF development registry only.
They are still subject to change and primarily meant for testing.</p>

<#list devExtensions as ext>	
<a name="${ext.rowType}"></a>          
<div class="definition">	
  <div class="title">
  	<div class="head">
        <a href="extension.do?id=${ext.rowType}">${ext.title}</a>
  	</div>
  </div>
  <div class="body">
      	<div>
			${ext.description!}
			<#if ext.link?has_content><br/>See also <a href="${ext.link}">${ext.link}</a></#if>              	
      	</div>
      	<div class="details">
      		<table>
          		<tr><th>Properties</th><td>${ext.properties?size}</td></tr>
          		<tr><th>Name</th><td>${ext.name}</td></tr>
          		<tr><th>Namespace</th><td>${ext.namespace}</td></tr>
          		<tr><th>RowType</th><td>${ext.rowType}</td></tr>
          		<tr><th>Keywords</th><td>${ext.subject!}</td></tr>
      		</table>
      	</div>
  </div>
</div>
</#list>
<#include "/WEB-INF/pages/inc/footer.ftl">
