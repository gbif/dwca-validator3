<#include "/WEB-INF/pages/inc/header.ftl">
	<title>EML Validation</title>
<#assign currentMenu = "eml" />
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>EML Validation</h1>

<p>You can either copy paste or upload an <a href="https://raw.githubusercontent.com/gbif/registry/master/registry-metadata/src/test/resources/eml-metadata-profile/sample.xml">eml document</a> into the form below to validate the xml against both the GBIF profile xml schema and the official EML schema.</p>
<p>For further validation please also see the <a href="http://knb.ecoinformatics.org/emlparser/">KNB EML parser</a>.</p>

<form action="eml.do" method="post" enctype="multipart/form-data">
<textarea name="meta" cols="125" rows="15">
</textarea>  
  <@s.file name="file" />
  
  <div class="buttons">
   	<input type="submit" name="validate" value="Validate"/>
  </div>
</form>

<#include "/WEB-INF/pages/inc/footer.ftl">
