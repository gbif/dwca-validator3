#
# EXTENSION TERMS AS TAB FILE FOR ${vocabulary.title}
# URI: ${vocabulary.uri}
#
IDENTIFIER	DESCRIPTION	LINK	PREFERRED TERMS	ALTERNATIVE TERMS
<#escape x as x?replace("[\\t\\n\\r\\s]+"," ","r")>
<#list vocabulary.concepts as c>	
${c.identifier}	${c.description!}	${c.link!}	<#list c.preferredTerms as t>${t.title} [${t.lang}]; </#list>	<#list c.alternativeTerms as t>${t.title} [${t.lang}]; </#list>
</#list>
</#escape>
