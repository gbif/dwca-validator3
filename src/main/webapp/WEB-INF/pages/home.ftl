<#include "/WEB-INF/pages/inc/header.ftl">
	<title>Darwin Core Archive Validator</title>
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>Darwin Core Archive Validator</h1>

<p style="width: 80%; margin: 1em auto; padding: 1em; font-size: 1.15em; line-height: 1.3; background: #fcc; border: 4px solid red;">Users publishing data to GBIF should use the <strong><a href="https://www.gbif.org/tools/data-validator">GBIF data validator</a></strong>.  That newer validator checks the structure <em>and data content</em> of archives, to find mistakes with coordinates, scientific names, dates and so on.<br><br>

This validator only checks the archive structure, but is retained for non-GBIF use of Darwin Core Archives.</p>

<p>This validator verifies the structural integrity of a <a href="https://github.com/gbif/ipt/wiki/DwCAHowToGuide">Darwin Core Archive</a>.  It does not check the data values, such as coordinates, dates or scientific names.</p>

<p>To validate a <a href="https://github.com/gbif/ipt/wiki/DwCAHowToGuide">Darwin Core Archive</a> file either provide a URL to an archive or upload an archive including data files for validation.</p>
<p>Please note that we limit the size of uploaded files to 100MB, so reduce your data files if necessary.
    We will happily pull bigger archives from a URL provided.</p>

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
