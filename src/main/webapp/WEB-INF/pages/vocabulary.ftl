<#include "/WEB-INF/pages/inc/header.ftl">
	<title>${vocabulary.title}</title>
<#assign currentMenu = "extensions" />
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>${vocabulary.title}</h1>

<p>${vocabulary.description!}</p>
<#if vocabulary.subject?has_content>
<p>Keywords: ${vocabulary.subject}</p>
</#if>
<#if vocabulary.link?has_content>
<p>Link: <a href="${vocabulary.link}">${vocabulary.link}</a></p>
</#if>

<p>
	<a href="vocabularyTxt.do?id=${vocabulary.uri}"><button>download</button></a>
</p>
<br/>

<h1>Concepts</h1>
                               
<#list vocabulary.concepts as c>	
<a name="${c.identifier}"></a>          
<div class="definition">	
  <div class="title">
  	<div class="head">
		${c.identifier}
  	</div>
  </div>
  <div class="body">
  		<#if c.description?has_content>
      	<div>
			${c.description!}
      	</div>
      	</#if>
  		<#if c.link?has_content>
      	<div>
			See also: <a href="${c.link}">${c.link}</a>              	
      	</div>
      	</#if>
      	<div>
          	Preferred Terms:
          	<em><#list c.preferredTerms as t>${t.title} <span class="small">[${t.lang}]</span>; </#list></em>
      	</div>
      	<div>
          	Alternative Terms: 
          	<em><#list c.alternativeTerms as t>${t.title} <span class="small">[${t.lang}]</span>; </#list></em>
      	</div>
  </div>
</div>
</#list>

<#include "/WEB-INF/pages/inc/footer.ftl">
