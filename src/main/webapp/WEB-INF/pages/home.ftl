<#include "/WEB-INF/pages/inc/header.ftl">
	<title>Darwin Core Archive Validator</title>
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>Darwin Core Archive Validator</h1>

<p>This validator verifies the structural integrity of a <a href="https://github.com/gbif/ipt/wiki/DwCAHowToGuide">Darwin Core Archive</a>. 
Please also see the nerwer <a href="https://www.gbif.org/tools/data-validator">GBIF data validator</a> which validates the content held within an archive. 
It is expected in time, that the newer validator will replace this version fully. 

<p>To validate a <a href="https://github.com/gbif/ipt/wiki/DwCAHowToGuide">Darwin Core Archive</a> file either provide a url to an archive or upload an archive including data files for validation.</p>
<p>Please note that we limit the size of uploaded files to 100MB, so reduce your data files if necessary.
    We will happily pull bigger archives from a url provided.</p>

<form action="validate.do" method="post" enctype="multipart/form-data">
	<h2>Validate archive URL:</h2>
	<input type="text" size="100" name="archiveUrl" value="http://rs.gbif.org/datasets/german_sl.zip"/>
  <input class="button-right" type="submit" name="validate" value="Validate"/>


	<h2>Upload local archive:</h2>
  <@s.file name="file" />

  <input class="button-right" type="submit" name="validate" value="Validate"/>
</form>

<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>

<#include "/WEB-INF/pages/inc/footer.ftl">
