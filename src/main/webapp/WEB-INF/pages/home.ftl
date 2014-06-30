<#include "/WEB-INF/pages/inc/header.ftl">
	<title>Darwin Core Archive Validator</title>
<#include "/WEB-INF/pages/inc/menu.ftl">

<h1>Darwin Core Archive Validator</h1>

<p>You can either copy paste a meta.xml descriptor into the form below, provide a url to an archive or upload a full darwin core archive including data files for validation.</p>
<p>Please note that we limit the size of uploaded files to 20MB, so reduce your data files if necessary. We will happily pull bigger archives from a url provided. If you need an archive for testing you can <a href="http://darwincore.googlecode.com/svn/trunk/dwca-reader/src/test/resources/archive-tax.zip">download a test archive</a> first.</p>

<form action="validate.do" method="post" enctype="multipart/form-data">
	<h3>Copy paste meta.xml</h3>
	<textarea name="meta" cols="125" rows="15">
<?xml version='1.0' encoding='utf-8'?>
<archive xmlns="http://rs.tdwg.org/dwc/text/"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://rs.tdwg.org/dwc/text/ http://rs.tdwg.org/dwc/text/tdwg_dwc_text.xsd">

  <core encoding="UTF-8" fieldsTerminatedBy="\t" linesTerminatedBy="\n" fieldsEnclosedBy='' ignoreHeaderLines="0" rowType="http://rs.tdwg.org/dwc/terms/Taxon">
    <files>
      <location>taxa.txt</location>
    </files>
    <id index="0" />
    <field index="2" term="http://rs.tdwg.org/dwc/terms/scientificName"/>
    <field index="3" term="http://rs.tdwg.org/dwc/terms/taxonomicStatus"/>
    <field index="4" term="http://rs.tdwg.org/dwc/terms/acceptedTaxonID"/>
    <field index="5" term="http://rs.tdwg.org/dwc/terms/acceptedTaxon"/>
    <field index="6" term="http://rs.tdwg.org/dwc/terms/taxonRank"/>
    <field index="7" term="http://rs.tdwg.org/dwc/terms/higherTaxonID"/>
    <field index="8" term="http://rs.tdwg.org/dwc/terms/taxonAccordingTo"/>
    <field default="ICBN" term="http://rs.tdwg.org/dwc/terms/nomenclaturalCode"/>
  </core>

  <extension encoding="UTF-8" fieldsTerminatedBy="\t" linesTerminatedBy="\n" fieldsEnclosedBy='' ignoreHeaderLines="0" rowType="http://rs.gbif.org/terms/1.0/Distribution">
    <files>
      <location>distribution.txt</location>
    </files>
    <coreid index="0" />
    <field index="1" term="http://rs.tdwg.org/dwc/terms/occurrenceStatus"/>
    <field index="2" term="http://rs.tdwg.org/dwc/terms/locationID"/>
    <field default="DE" term="http://rs.tdwg.org/dwc/terms/country"/>
  </extension>

  <extension encoding="UTF-8" fieldsTerminatedBy="\t" linesTerminatedBy="\n" fieldsEnclosedBy='' ignoreHeaderLines="0" rowType="http://rs.gbif.org/terms/1.0/VernacularName">
    <files>
      <location>vernacular.txt</location>
    </files>
    <coreid index="0" />
    <field index="2" term="http://purl.org/dc/terms/language"/>
    <field index="3" term="http://rs.tdwg.org/dwc/terms/locality"/>
  </extension>
</archive>

</textarea>

	<h3>Validate full online archive</h3>
	<input type="text" size="100" name="archiveUrl" value=""/>

	<h3>Upload local archive or meta.xml</h3>
  <@s.file name="file" />

	<br/><br/>
	<div class="buttons">
   	<input type="submit" name="validate" value="Validate"/>
  </div>
</form>

<#include "/WEB-INF/pages/inc/footer.ftl">
