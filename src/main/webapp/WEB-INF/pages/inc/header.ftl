[#ftl]
[#setting url_escaping_charset="UTF-8"]
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
	    <meta name="copyright" lang="en" content="GBIF" />
 		<link rel="stylesheet" type="text/css" href="styles/style.css"/>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>	
		<script type="text/javascript" src="js/global.js"></script>
 		
[#-- GOOGLE ANALYTICS - asynchroneous: http://code.google.com/apis/analytics/docs/tracking/asyncTracking.html --]
[#if (cfg.analyticsKey!"")?length>1] 
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '${cfg.analyticsKey}']);
  _gaq.push(['_trackPageview']);
  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
[/#if]

[#assign currentMenu = "home"]