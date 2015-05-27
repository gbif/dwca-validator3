<#-- @ftlvariable name="" type="org.gbif.dwca.action.ValidateAction" -->
<#assign ext=(extensions.get(af.rowType)!"") />
<#-- EXTENSION -->
<#if ext != "">
  <h3>${ext.title} <span class="small"><a href="extension.do?id=${af.rowType}" target="_blank">${af.rowType}</a></span></h3>
  <#if ext.dev>
  <p><img src="images/warning.gif"/> <em>Extension is still under development</em></p>
  </#if>
<#else>
  <h3>Entity <span class="small">${af.rowType}</span></h3>
  <p class="warn">The extension of row type <em>${af.rowType}</em> is unknown to GBIF.</p>
</#if>

<#-- SOURCE DATA FILES -->
<p>
<#if (af.locations?size>1)><span class="warn">There are ${af.locations?size} files allocated - although legal according to dwca specs this is not supported yet</span>
<#else>
	<#if (af.locations?size==1)>
		<#if af.location??>
			<#if metaOnly || dwcaFiles.contains(af.location)>
			The entity is mapped to source file <em>${af.location}</em>.</span>
			<#else>
			<span class="warn">The declared source file <em>${af.location}</em> cannot be found in the archive.</span>
			</#if>
		<#else>
			The archive is a <em>single data file</em> only.</span>
		</#if>
	<#else>
	<span class="warn">There is no source file assigned to this entity.</span>
	</#if>
</#if>
</p>

<#-- FIELDS -->
<ul>
<#-- ID -->
<li><em>Core Record ID</em> mapped to
	<#if af.id?exists && af.id.index?exists>column ${af.id.index}
	<#else><span class="warn">nothing</span>
	</#if>
</li>
<#-- ok -->
<#list action.getFields(af.rowType) as f>
<li><pre>${f.term.qualifiedName()}</pre> mapped to
	<#if f.index?exists>column ${f.index}<#if f.defaultValue?exists> with default value &quot;${f.defaultValue}&quot;</#if>
	<#else><#if f.defaultValue?exists>constant value &quot;${f.defaultValue}&quot; <#else><span class="warn">nothing</span></#if>
	</#if>
</li>
</#list>
<#-- unknown -->
<#list action.getFieldsUnknown(af.rowType) as f>
<li><span class="warn">Unknown term</span> <pre>${f.term.qualifiedName()}</pre> mapped to
	<#if f.index?exists>column ${f.index}<#if f.defaultValue?exists> with default value &quot;${f.defaultValue}&quot;</#if>
	<#else><#if f.defaultValue?exists>constant value &quot;${f.defaultValue}&quot; <#else><span class="warn">nothing</span></#if>
	</#if>
</li>
</#list>
<#-- missing -->
<#list action.getFieldsMissing(af.rowType) as f>
<li><span class="warn">Missing required term</span> <pre>${f.qualname}</pre></li>
</#list>
</ul>
