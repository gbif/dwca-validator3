<#include "/WEB-INF/pages/inc/header.ftl">
<title>DwC-A Validation Result</title>
<style>
	h1 {
		margin-bottom: 0px;
	}
	h1 > h2 {
		margin-top: 10px;
	}
	table.dwc td{
		text-align: left;
		margin:0;
		padding:0;
		border-top: 1px dotted #000;
		border-right: 1px dotted #000;
	}
	table.dwc th{
		background-color: #eee;
		font-weight: bold;
	}
	table.dwc tr.concept th{
		text-decoration: underline;
	}
	table.dwc tr.core td, table.dwc tr.core th{
		border-top: 2px solid #000;
	}
</style>
<#include "/WEB-INF/pages/inc/menu.ftl">

<#include "/WEB-INF/pages/validate_core.ftl">

<#include "/WEB-INF/pages/inc/footer.ftl">
