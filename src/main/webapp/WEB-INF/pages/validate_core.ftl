<#-- @ftlvariable name="" type="org.gbif.dwca.action.ValidateAction" -->
<h1>DwC Archive Validation Result</h1>

<p><strong>Archive Source</strong>: <#if archiveUrl?has_content>${archiveUrl}<#else><#if fileFileName?has_content>uploaded file ${fileFileName}<#else>meta.xml form</#if></#if></p>
<#if !online>
<p class="warn">${offlineReason!"Failed to download archive"}</p>
</#if>

<p><strong>Date validated</strong>: ${now?datetime?string.medium}</p>

<p>This report has been written to <a href="${reportUrl!}">${reportUrl!}</a> which will be deleted after one month. Until then you can revisit the report at your convenience.</p>

<#if dwcaException?exists>
<p class="warn">Archive could not be read</p>
<pre>${dwcaException}</pre>
<pre>
	<#list dwcaStackTrace as el>
${el}
</#list>
</pre>
</#if>

<h2>Descriptor meta.xml</h2>
<#if metaExists>
<p>Validating against the dwc text guidelines <a href="${dwcaSchema}">xml schema</a></p>
	<#if schemaException?exists>
	<p class="warn">Validation error</p>
	<pre>${schemaException}</pre>
	<pre>
		<#list schemaStackTrace as el>
${el}
</#list>
	</pre>

	<#else>
		<p class="good">Validation successful</p>
	</#if>
<#else>
	<p>Could not find any meta.xml archive descriptor to validate.</p>
</#if>


<#if archive?exists>
<p class="good">Archive read successfully</p>

<h2>Metadata</h2>
<p>An archive should (not required) have a metadata file bundled that informs about the whole dataset.
	GBIF recommends a <a href="${schemaEmlGbifUrl}">subset of EML</a>, but simple <a
					href="http://code.google.com/p/darwincore/source/browse/trunk/dwca-reader/src/test/resources/metadata/worms_dc.xml">Dublin
		Core</a> is also permitted.
</p>
	<#if archive.metadataLocation?exists>
		<#if metadataException?exists>
		<p class="warn">Metadata could not be read</p>
		<pre>${metadataException}</pre>
		<pre>
			<#list metadataStackTrace as el>
${el}
</#list>
		</pre>
		</#if>
		<#if metadata??>
		<p class="good">Dataset metadata description read from file <em>${archive.metadataLocation}</em> .</p>
		<div class="details">
      <#include "/WEB-INF/pages/inc/metadata.ftl">
		</div>
			<#else>
			<p class="warn">Cant read dataset metadata.</p>
		</#if>
		<#else>
		<p class="warn">No dataset metadata description found.</p>
	</#if>

<h2>Mappings</h2>
<p>Inspecting the individual archive files and comparing the mapped concepts to the extensions registered with GBIF.
	An archive may have additional terms mapped than the ones declared by an extension. But those additions will not be
	understood widely so be careful!
</p>

	<#if archive.core?exists>
		<#assign af=archive.core />
		<#include "/WEB-INF/pages/validate_entity.ftl">
		<#else>
		<p class="warn">The archive has no core data file mapped!</p>
	</#if>

	<#list archive.extensions as af>
		<#include "/WEB-INF/pages/validate_entity.ftl">
	</#list>

<h2>Archive Data Files</h2>
<p>Inspecting the archive using the <a href="https://github.com/gbif/dwca-io">dwca-io library</a>.
	The archive contains <#if archive.core?exists>a<#else>no</#if> core and ${archive.extensions?size} extension(s).
</p>

	<#if metaOnly>
		<p>meta.xml</p>
	<#else>
		<#list dwcaFiles as fn>
			<h3>${fn}</h3>
			<p>The data file contains ${fileLines[fn]!"unknown"} rows with ${fileColumns[fn]!"unknown"} columns.</p>

				<#if brokenLines[fn]?exists && (brokenLines[fn]?size > 0)>
				<p class="warn">The data file contains at least ${brokenLines[fn]?size} rows with different column numbers:</p>
				<ul>
					<#list brokenLines[fn]?keys as key>
						<#assign val=brokenLines[fn].get(key) />
						<li>
							<strong>Line ${key?c} </strong> with ${val?size} columns<br/>
							<pre><#list val as col>${col}	</#list></pre>
						</li>
					</#list>
				</ul>
					<#else>
					<p class="good">All rows in the data file have the same number of columns.</p>
				</#if>

				<#if archive.core?exists && (dwcaFiles?size==1 || (archive.core.location!"")==fn)>

					<#if (nonUniqueId?size > 0)>
					<p class="warn">The core data file contains non unique core IDs:</p>
					<ul>
						<#list nonUniqueId as key>
							<li>
								<pre>${key!"NULL"}</pre>
							</li>
						</#list>
					</ul>
						<#else>
							<#if tooManyCoreIds>
							<p class="good">All core record ids seem unique, but the number of core records exceeds the maximum number
								this validator can verify. Not all ids tested!</p>
								<#else>
								<p class="good">All core record ids are unique.</p>
							</#if>
					</#if>

					<#else>

						<#if missingIds[fn]?exists && (missingIds[fn]?size > 0)>
						<p class="warn">The extension data file contains references to core IDs that do not exist:</p>
						<ul>
							<#list missingIds[fn] as key>
								<li>
									<pre>${key!"NULL"}</pre>
								</li>
							</#list>
						</ul>
							<#else>
								<#if tooManyCoreIds>
								<p class="good">The number of core records exceeds the maximum number this validator can verify. We
									therefore cannot test the referential integrity of the extension records.</p>
									<#else>
									<p class="good">All extension records refer to an existing core record.</p>
								</#if>
						</#if>


				</#if>
			</#list>
	</#if>


  <#if (brokenRefIntegrity?size > 0)>
  <h2>Referential Integrity</h2>
  <p>Verifying that mapped ID terms reference existing core records.</p>
    <#if tooManyCoreIds>
    <p class="warn">The number of core records exceeds the current limit for validation. Skipping the referential
      integrity test.</p>
    <#else>
      <#list brokenRefIntegrity?keys as term>
      <h3>${term}</h3>
        <#assign ids = brokenRefIntegrity[term] />
        <#if ids?size == 0>
        <p class="good">All values of term ${term} have a matching core record.</p>
          <#else>
          <p class="warn">Values of term ${term} contains references to non existing core IDs:</p>
          <ul>
            <#list ids as id>
              <li>${id!"NULL"}</li>
            </#list>
          </ul>
        </#if>
      </tr>
      </#list>
    </#if>
  </#if>

  <#if acceptedSynonyms??>
  <h2>Synonyms of Synonyms</h2>
  <p>Verifying that no acceptedNameUsageID records are synonyms.</p>
    <#if tooManyCoreIds>
      <p class="warn">The number of core records exceeds the current limit for validation. Skipping the synonym of synonym test.</p>
    <#else>
        <#if acceptedSynonyms?size == 0>
          <p class="good">All acceptedNameUsageID records are accepted.</p>
        <#else>
          <p class="warn">Core ids of synonyms referencing another synonym as the accepted name usage:</p>
          <ul>
            <#list acceptedSynonyms as id>
              <li>${id!"NULL"}</li>
            </#list>
          </ul>
        </#if>
    </#if>
  </#if>

  <#if parentSynonyms??>
  <h2>Accepted Parents</h2>
  <p>Verifying that all parentNameUsageID records are accepted.</p>
    <#if tooManyCoreIds>
      <p class="warn">The number of core records exceeds the current limit for validation. Skipping the accepted parent test.</p>
    <#else>
        <#if parentSynonyms?size == 0>
          <p class="good">All parentNameUsageID records are accepted.</p>
        <#else>
          <p class="warn">Core ids of records referencing a synonym as the parent name usage:</p>
          <ul>
            <#list parentSynonyms as id>
              <li>${id!"NULL"}</li>
            </#list>
          </ul>
        </#if>
    </#if>
  </#if>

  <h2>Null Values</h2>
  <p>Verifying that no verbatim null values are found in the data. For example NULL or \N</p>
  <#if (nullValues.size() == 0)>
    <p class="good">All null values are empty strings.</p>
  <#else>
    <p class="warn">Suspicious values and their counts found which are often used for NULL. Consider using empty strings instead:</p>
    <ul>
      <#list nullValues.keys() as nullVal>
        <li>${nullVal!""} &nbsp;&nbsp;[${nullValues.get(nullVal)}]</li>
      </#list>
    </ul>
  </#if>



	<#if records?exists && (records?size>0)>
	<h2>Scan Records</h2>
	<p>Reading the first ${scanSize} records from the archive using the dwca-io library:</p>

	<table class="dwc">
		<#list extensionOrder as rowType>
			<tr class="concept">
				<th>${rowType}</th>
				<#list action.getRowHeader(rowType) as ct>
					<th>${ct!}</th></#list>
			</tr>
		</#list>
		<#list records as star>
			<#list star as rec>
				<#if rec_index==0>
         <tr class="core">
					<#else>
			 	 <tr>
				</#if>
				<#list rec as col>
          <#if col_index==0>
					  <th>${col!""}</th>
          <#else>
						<td>${col!"NULL"}</td></#if></#list>
			   </tr>
			</#list>
		</#list>
	</table>
	</#if>
	<#if recordsException?exists>
	<p class="warn">Error scanning records:</p>
	<pre>${recordsException}</pre>
	<pre>
		<#list recordsStackTrace as el>
${el}
</#list>
	</pre>
	</#if>

</#if>
