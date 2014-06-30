#
# EXTENSION TERMS AS TAB FILE FOR ${extension.title}
# ROWTYPE: ${extension.rowType}
#
TERM	QUALIFIED TERM	DESCRIPTION	EXAMPLES	LINK	DATA TYPE	REQUIRED
<#escape x as x?replace("[\\t\\n\\r\\s]+"," ","r")>
<#list extension.properties as p>
${p.name}	${p.qualname}	${p.description!}	${p.examples!}	${p.link!}	${(p.vocabulary.uri)!p.type!"string"}	${p.required?string}
</#list>
</#escape>