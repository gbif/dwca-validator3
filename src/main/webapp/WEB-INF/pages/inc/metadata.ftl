<#-- @ftlvariable name="metadata" type="org.gbif.api.model.registry.Dataset" -->
<#macro cat xlist>
<#list xlist as x>${x!}<#if x_has_next>, </#if></#list>
</#macro>

<table>
    <tr><th>Title</th><td>${metadata.title!}</td></tr>
    <tr><th>Description</th><td>${metadata.description!}</td></tr>
    <tr><th>Citation</th><td>${metadata.citation!}</td></tr>
    <tr><th>DOI</th><td>${metadata.doi!}</td></tr>
    <tr><th>Homepage</th><td>${metadata.homepage!}</td></tr>
    <tr><th>LogoUrl</th><td>${metadata.logoUrl!}</td></tr>
    <tr><th>Rights</th><td>${metadata.rights!}</td></tr>
    <tr><th>Publication Date</th><td><#if metadata.pubDate??>${metadata.pubDate?date}</#if></td></tr>
<#list metadata.contacts as c>
    <tr><th>Contact ${c.type!}</th><td>${c.firstName!} ${c.lastName!} ${c.organization!}; <@cat c.address/> ${c.postalCode!} ${c.city!} ${c.country!}; <@cat c.email/> <@cat c.phone/></td></tr>
</#list>
</table>