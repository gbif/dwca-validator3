[#ftl]
[#include "/WEB-INF/pages/inc/header.ftl"/]
 <title>DwCA Validator API</title>
[#assign menu = "api"]
[#include "/WEB-INF/pages/inc/menu.ftl"/]

<div>
	<h1>DwCA Validator API</h1>
	<h4>https://tools.gbif.org/dwca-validator/validatews.do</h4>
    <div class="boxed">
		<p>
		The validator can also be used as a json webservice to validate online archives and will return some basic validation information along with a link to the saved regular html report that is stored for one month.
		</p>

		<br/>
		<h2>Request Parameters</h2>
		<dl>
			<dt>archiveUrl</dt>
			<dd>The full public url to the archive to be validated</dd>

			<dt>ifModifiedSince</dt>
			<dd>An optional ISO date (yyyy-mm-dd) to enable conditional get requests, validating archives only if they have been modified since the given date. This feature requires the archive url to honor the if-modified-since http header. Apache webservers for example do this out of the box for static files, but if you use dynamic scripts to generate the archive on the fly this might not be recognised.</dd>
		</dl>

			<dt>reportId</dt>
			<dd>An optional identifier for the report to be generated - if not given some automatic unique value will be given. If you use this parameter make sure your identifier is globally unique and will not clash with other report ids used as the validator does not check for existing reports. It will overwrite any existing report with the same id! Urls and UUIDs are good candidates if you really want your own id - otherwise better use the automatically generated one.</dd>

	    <br/>

		<h2>JSON Response</h2>
		<p>
		Example of a successful validation response with request =
			https://tools.gbif.org/dwca-validator/validatews.do?archiveUrl=http://rs.gbif.org/datasets/vernaculars/vernacular_registry_dwca_3.zip
		</p>
<pre>
{
  "archiveUrl": "http://rs.gbif.org/datasets/vernaculars/vernacular_registry_dwca_3.zip",
  "httpStatusCode": 200,
  "online": true,
  "valid": true,
  "metadata": true,
  "reportId": "210-308359173575056734",
  "report": "https://tools.gbif.org/dwca-reports/210-308359173575056734.html",
  "fileRecords": {
      "vernaculars.txt": 1315,
      "Taxa.txt": 1306
  },
  "coreRecords": 1306
}
</pre>

			<p>
			Example of a not modified validation response with request =
				https://tools.gbif.org/dwca-validator/validatews.do?archiveUrl=http://rs.gbif.org/datasets/vernaculars/vernacular_registry_dwca_3.zip&ifModifiedSince=2011-06-27
			</p>
<pre>
{
  "archiveUrl": "http://rs.gbif.org/datasets/vernaculars/vernacular_registry_dwca_3.zip",
  "httpStatusCode": 304,
  "online": true
}
</pre>

			<dl>
			<dt>archiveUrl</dt>
			<dd>
			    The requested archiveUrl
			</dd>

			<dt>httpStatusCode</dt>
			<dd>
				The http status code when accessing the archiveUrl with http GET.
			</dd>

			<dt>online</dt>
			<dd>
				A simple boolean to indicate whether the archive url was available or offline.
			</dd>


			<dt>valid</dt>
			<dd>
				A simple boolean to indicate whether the archive was valid. False if any validation error occurred.
			</dd>

			<dt>metadata</dt>
			<dd>
				A simple boolean to indicate whether the archive contain readable metadata.
			</dd>

			<dt>reportId</dt>
			<dd>
				The identifier for the generated validation html report.
			</dd>

			<dt>report</dt>
			<dd>
				The url to the generated html report.
			</dd>

			<dt>coreRecords</dt>
			<dd>
				The number of records in the core data files if it exists and is readable.
			</dd>

			<dt>fileRecords</dt>
			<dd>
				The number of records for each of the data files found in the archive.
			</dd>


		</dl>
	</div>

</div>

[#include "/WEB-INF/pages/inc/footer.ftl"/]
