<#-- @ftlvariable name="" type="org.gbif.dwca.action.AboutAction" -->
<#include "/WEB-INF/pages/inc/header.ftl"/>
 <title>The Darwin Core Archive Validator</title>
 <style>
 p{
 	font-size:14px;
 	line-height:16px;
 }
 </style>
<#assign currentMenu = "about" />
<#include "/WEB-INF/pages/inc/menu.ftl"/>

<h1>The Darwin Core Archive Validator</h1>

<p>This validator was written by <a href="http://www.gbif.org">GBIF</a> to test Darwin Core Archives 
as specified in the <a href="http://rs.tdwg.org/dwc/terms/guides/text/index.htm">Darwin Core Text Guide</a>.
Due to the simplicity of the archives GBIF encourages publishers to create them using simple custom scripts. 
Therefore the need arises to provide a testing framework for developers to make sure GBIF and others can read the information as expected.
 </p>
 
 <p>The validator uses the <a href="${cfg.metaSchema}">official XML schema</a> to validate the meta.xml descriptor,
 but additionally it uses the <a href="https://github.com/gbif/dwca-reader">Darwin Core Archive Reader</a> java library to validate the content against the known extensions and terms registered within the GBIF network for sharing biodiversity data.
 GBIF runs a production and a development registry that keeps track of extensions, both of which are used by this validator. 
 You can find more informatin about known extensions <a href="extensions.do">on this site</a> or 
 inspect the source defintions as listed by the GBIF <a ref="${cfg.prodExtensions}">production</a>
 or <a ref="${cfg.devExtensions}">development</a> resources.
 </p>

<p>
GBIF recommends to bundle an Ecological Markup Language (<a href="http://knb.ecoinformatics.org/software/eml/">EML</a>) xml file with an archive. 
As EML is a rather large and complex schema we have specified a GBIF profile that uses a subset of EML 
and also declares specific additions to EML within the generic additionalMetadata section of EML. 
Every valid GBIF profile document should therefore always be valid according to the official EML schema.
The <a href="eml.do">EML validation</a> is done according those two xml schemas. 
The <a href="${schemaEmlGbifUrl}">GBIF EML profile</a> and the <a href="${schemaEmlUrl}">official EML schema</a>. See <a href="http://knb.ecoinformatics.org/software/download.html#eml">download area</a> for EML. 
</p>

<p>The eml and dwca xml schemas have been last updated on ${lastSchemaUpdate?datetime?string.medium}</p> 

<p>For more information please contact <a mailto="mdoering@gbif.org">Markus Döring</a>, GBIF. </p>

<#include "/WEB-INF/pages/inc/footer.ftl"/>
