[#ftl]
[#setting url_escaping_charset="UTF-8"]
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta name="copyright" lang="en" content="GBIF"/>
	<meta name="author" content="dwca-validator ${cfg.version!"???"}"/>
	<meta name="keywords" content="valid=${(valid!false)?string},online=${(online!false)?string},metadata=${(metadata?exists)?string}"/>
	<base href="http://tools.gbif.org/dwca-validator/"/>
	<link rel="stylesheet" type="text/css" href="styles/style.css"/>
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

</head>
<body>
	<div id="wrapper">

		<div id="logo">
			<a href="home.do"><img src="styles/logo.jpg"></a>
		</div>

		<div id="content">


		[#include "/WEB-INF/pages/validate_core.ftl"]


		</div>
	</div>
</body>
</html>
