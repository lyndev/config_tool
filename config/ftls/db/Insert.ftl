INSERT INTO `${tabelName}` VALUES (
<#list datas as data>
    <#if data_has_next>
	<#if fields[data_index].className=="int">
	    ${data},
	<#elseif fields[data_index].className=="bigint">
	    ${data},
	<#elseif fields[data_index].className=="smallint">
	    ${data},
	<#elseif fields[data_index].className=="tinyint">
	    ${data},
	<#else>
	    '${data}',
	</#if>
    <#else>
	<#if fields[data_index].className=="int">
	    ${data}
	<#elseif fields[data_index].className=="bigint">
	    ${data}
	<#elseif fields[data_index].className=="smallint">
	    ${data}
	<#elseif fields[data_index].className=="tinyint">
	    ${data}
	<#else>
	    '${data}'
	</#if>
  </#if>
</#list>
);