<#setting url_escaping_charset="UTF-8">
{
"archiveUrl":"${archiveUrl!"missing"}",
"online":${online?string("true", "false")}
<#if status??>
,"httpStatusCode":${status.statusCode}
<#if status.statusCode!=304>
,"valid":${valid?string("true", "false")},
"metadata":${(metadata?exists)?string("true", "false")},
"reportId":"${reportId}",
"report":"${reportUrl!"no report"}"
<#if dwcaFiles?exists>
,"fileRecords":{<#list dwcaFiles as fn>"${fn}":${(fileLines[fn]!-1)?c}<#if fn_has_next>,</#if></#list>}
,"coreRecords":${(fileLines[coreFile!]!-1)?c}
</#if>
</#if>
</#if>
}

