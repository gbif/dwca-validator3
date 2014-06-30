/***************************************************************************
 * Copyright 2010 Global Biodiversity Information Facility Secretariat
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ***************************************************************************/

package org.gbif.dwca.action;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.text.StarRecord;
import org.gbif.dwca.model.Extension;
import org.gbif.dwca.model.ExtensionProperty;
import org.gbif.dwca.service.ExtensionManager;
import org.gbif.dwca.service.ValidationService;
import org.gbif.dwca.utils.FreemarkerUtils;
import org.gbif.dwca.utils.UrlUtils;
import org.gbif.file.CSVReader;
import org.gbif.metadata.BasicMetadata;
import org.gbif.metadata.DateUtils;
import org.gbif.metadata.eml.Eml;
import org.gbif.metadata.eml.EmlFactory;
import org.gbif.utils.HttpUtil;
import org.gbif.utils.collection.CompactHashSet;
import org.gbif.utils.file.ClosableIterator;
import org.gbif.utils.file.CompressionUtil;
import org.gbif.utils.file.CompressionUtil.UnsupportedCompressionType;
import org.gbif.utils.file.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.google.inject.Inject;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.struts2.views.freemarker.StrutsBeanWrapper;

/**
 * @author markus
 *
 */
public class ValidateAction extends BaseAction {
  protected static final Pattern NULL_REPL = Pattern.compile("^\\s*(null|\\\\N|\\s)\\s*$", Pattern.CASE_INSENSITIVE);

  private static final String REPORTS_DIR_KEY = "reports.dir";
  private static final String REPORTS_WWW_KEY = "reports.www";

  private File file;
  private String fileContentType;
  private String fileFileName;
  private String meta;
  private String archiveUrl;
  private String ifModifiedSince;
  private Date ifModifiedSinceDate;
  @Inject
  private ValidationService validation;
  @Inject
  private ExtensionManager extensionManager;
  @Inject
  private HttpUtil http;
  @Inject
  private Configuration fm;

  // additional webservice params
  private String reportId;
  private String reportUrl;

  // results

  private Date now = new Date();
  // did archive validation succeed overall?
  private boolean valid = true;
  // is archive the online accessible via the given url?
  private boolean online = true;
  private String offlineReason;
  // meta.xml
  private boolean metaExists = true;
  private boolean metaOnly = true;
  private Exception schemaException;
  private List<StackTraceElement> schemaStackTrace;
  // archive factory
  private Exception dwcaException;
  private List<StackTraceElement> dwcaStackTrace = new ArrayList<StackTraceElement>();
  private Archive archive;
  private Map<String, Extension> extensions;
  private Map<String, List<ArchiveField>> fields = new HashMap<String, List<ArchiveField>>();
  private Map<String, List<ExtensionProperty>> fieldsMissing = new HashMap<String, List<ExtensionProperty>>();
  private Map<String, List<ArchiveField>> fieldsUnknown = new HashMap<String, List<ArchiveField>>();
  // as found in dwca folder
  private Set<String> dwcaFiles = new HashSet<String>(); // all files in dwca but meta.xml and the metadata one
  private String coreFile;
  // all of the following max 100
  private static final int MAX_RECORDS_REPORTED = 50;
  private static final int MAX_IDS_STORED = 2000000;
  // key=filename
  private Map<String, Map<Integer, String[]>> brokenLines = new HashMap<String, Map<Integer, String[]>>();
  private Map<String, Set<String>> missingIds = new HashMap<String, Set<String>>();
  private Map<String, Set<String>> brokenRefIntegrity = new HashMap<String, Set<String>>();
  private Map<String, Integer> fileLines = new HashMap<String, Integer>();
  private Map<String, Integer> fileColumns = new HashMap<String, Integer>();
  private Set<String> nonUniqueId = new CompactHashSet<String>();
  private TObjectLongHashMap<String> nullValues = new TObjectLongHashMap<String>();
  private Set<String> acceptedSynonyms;
  private Set<String> parentSynonyms;
  private Set<Integer> emptyLines;
  private TObjectByteMap<String> coreIds = new TObjectByteHashMap<String> ();
  // metadata
  private BasicMetadata metadata;
  private Exception metadataException;
  private ArrayList<StackTraceElement> metadataStackTrace = new ArrayList<StackTraceElement>();
  // for eml validation only
  private Eml eml;
  private Exception emlException;
  private boolean emlSchemaValidated = false;
  private boolean gbifSchemaValidated = false;
  private boolean tooManyCoreIds = false;

  // records
  private int scanSize = 100;
  private List<List<List<String>>> records = new ArrayList<List<List<String>>>();
  private List<String> extensionOrder = new ArrayList<String>();
  private Map<String, List<String>> recordsHeader = new TreeMap<String, List<String>>();
  private Exception recordsException;
  private ArrayList<StackTraceElement> recordsStackTrace = new ArrayList<StackTraceElement>();
  private StatusLine status;

  static class ArchiveLocation {

    public File dwcaFolder;
    public File metaFile;
  }

  public static File createDwcaDirectory() throws IOException {
    final File temp;

    temp = File.createTempFile("dwca-", Long.toString(System.nanoTime()));

    if (!(temp.delete())) {
      throw new IOException("Could not delete temp dwca file: " + temp.getAbsolutePath());
    }

    if (!(temp.mkdir())) {
      throw new IOException("Could not create temp dwca directory: " + temp.getAbsolutePath());
    }

    return (temp);
  }

  public String eml() throws Exception {
    if (file != null || meta != null) {
      validateEml();
      return SUCCESS;
    }
    return INPUT;
  }

  public Map<String, Set<String>> getBrokenRefIntegrity() {
    return brokenRefIntegrity;
  }

  public boolean isTooManyCoreIds() {
    return tooManyCoreIds;
  }

  public Map<String, Integer> getFileColumns() {
    return fileColumns;
  }


  private ArchiveLocation extractArchive() throws IOException{
    ArchiveLocation archLoc = null;
    if (fileFileName != null) {
      archLoc = openArchive(file, fileFileName);
    } else if (!StringUtils.isBlank(archiveUrl)) {
      // url to achive provided
      // extractArchive and validate
      URL url = new URL(UrlUtils.encodeURLWhitespace(archiveUrl.trim()));
      file = File.createTempFile("download-", ".dwca");
      // use conditional get?
      if (ifModifiedSinceDate!=null){
        log.debug("Use conditional get for download if modified since: " + ifModifiedSinceDate.toString());
        status = http.downloadIfModifiedSince(url, ifModifiedSinceDate, file);
      }else{
        status = http.download(url, file);
      }
      if (status.getStatusCode()== HttpStatus.SC_NOT_MODIFIED) {
        // not modified, no need to download and validate
        online = true;
        archLoc=null;
        addActionMessage("The archive hasn't been modified since "+ ifModifiedSinceDate.toString());
        addActionMessage("No download and validation done.");
      }else if (http.success(status)) {
        online = true;
        archLoc = openArchive(file, url.getFile());
      } else {
        String reason = "HTTP" + status.getStatusCode();
        if (status.getReasonPhrase() != null) {
          reason += " " + status.getReasonPhrase();
        }
        setOffline(reason);
      }
    } else if (meta != null) {
      // meta.xml provided as text
      archLoc = new ArchiveLocation();
      archLoc.metaFile = File.createTempFile("meta-", ".xml");
      archLoc.dwcaFolder = archLoc.metaFile;
      Writer w = FileUtils.startNewUtf8File(archLoc.metaFile);
      w.write(meta);
      w.close();
    }
    return archLoc;
  }

  @Override
  public String execute() {
    ArchiveLocation archLoc=null;
    try {
      if (!StringUtils.isBlank(ifModifiedSince)){
        ifModifiedSinceDate = DateUtils.parseIso(ifModifiedSince);
        if (ifModifiedSinceDate==null){
          log.debug("Use conditional get for download if modified since: " + ifModifiedSince);
          return INPUT;
        }
      }

      archLoc = extractArchive();
      if (archLoc==null && status==null){
        return INPUT;
      }
      if (archLoc!=null){
        extensions = extensionManager.map();
        validateAgainstSchema(archLoc.metaFile);
        validateArchive(archLoc.dwcaFolder);
      }
    } catch (MalformedURLException e) {
      setOffline("MalformedURLException "+e.getMessage());
    } catch (SocketException e) {
      setOffline(e.getClass().getSimpleName() + " " + e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error when validating archive", e);
      valid=false;
    } finally {
      // cleanup temp files!
      cleanupTempFile(archLoc);
    }

    // store html report
    if (archLoc != null) {
      storeReport();
    }

    return SUCCESS;
  }

  private void cleanupTempFile(ArchiveLocation archLoc){
    Collection<File> tempFiles = new ArrayList<File>();
    tempFiles.add(file);
    if (archLoc != null) {
      tempFiles.add(archLoc.metaFile);
      tempFiles.add(archLoc.dwcaFolder);
    }
    for (File f : tempFiles){
      if (f!=null && f.exists()){
        try {
          org.apache.commons.io.FileUtils.forceDelete(f);
        } catch (IOException e) {
          log.warn("Failed to remove temporary file/folder "+f.getAbsolutePath(), e);
        }
      }

    }
  }
  private void setOffline(String reason){
    valid=false;
    online=false;
    metaExists = false;
    offlineReason=reason;
  }
  /*
    copies the html result of this action into a file that is stored for subsequent access
   */
  private boolean storeReport() {
    if (reportId == null) {
      // create random report id
      Random rnd = new Random();
      reportId = String.format("%Tj-%d", new Date(), Math.abs(rnd.nextLong()));
    }
    File report = new File(cfg.getProperty(REPORTS_DIR_KEY), reportId +".html");
    reportUrl = cfg.getProperty(REPORTS_WWW_KEY)+"/"+ reportId + ".html";
    log.debug("Writing validation report to " + report.getAbsolutePath());
    try {
      BeansWrapper wrapper = new StrutsBeanWrapper(true);
      wrapper.setExposureLevel(0);
      fm.setObjectWrapper(wrapper);
      FreemarkerUtils.writeUtf8File(fm,report,"/WEB-INF/pages/validate_report.ftl",this);
      } catch (TemplateException e) {
        log.error("Cannot find template for validation report",e);
        return false;
      } catch (IOException e) {
        log.error("Cannot write validation report", e);
        return false;
      }
    return true;
  }

  private ArchiveLocation openArchive(File sourceFile, String originalFileName) throws IOException {
    ArchiveLocation loc = new ArchiveLocation();
    loc.dwcaFolder = createDwcaDirectory();
    List<File> files;
    try {
      files = CompressionUtil.decompressFile(loc.dwcaFolder, sourceFile);
      metaOnly = false;
      loc.metaFile = new File(loc.dwcaFolder, "meta.xml");
    } catch (UnsupportedCompressionType e) {
      // seems to be a single file
      // move single file into temp dir and rename it to original
      File f = new File(loc.dwcaFolder, originalFileName);
      org.apache.commons.io.FileUtils.moveFile(sourceFile, f);
      if (originalFileName.endsWith(".xml")) {
        // might be a meta.xml on its own?
        loc.metaFile = f;
        addActionMessage("Cannot decompress archive - treat like meta.xml on its own");
      } else {
        // some single text file?
        metaOnly = false;
        loc.metaFile = null;
        loc.dwcaFolder = f;
        addActionMessage("Cannot decompress archive - treat like single data file");
      }
    }
    return loc;
  }
  public Archive getArchive() {
    return archive;
  }

  public Exception getDwcaException() {
    return dwcaException;
  }

  public Set<String> getDwcaFiles() {
    return dwcaFiles;
  }

  public String getDwcaSchema() {
    return cfg.getMetaSchema();
  }

  public List<StackTraceElement> getDwcaStackTrace() {
    return dwcaStackTrace;
  }

  public Eml getEml() {
    return eml;
  }

  public Exception getEmlException() {
    return emlException;
  }

  private InputStream getEmlInputStream() throws FileNotFoundException {
    InputStream src = null;
    if (file != null) {
      // file upload
      src = new FileInputStream(file);
    } else {
      // copy paste in this.meta
      src = new StringBufferInputStream(meta);
    }
    return src;
  }

  private Source getEmlSource() throws FileNotFoundException {
    Source src = new StreamSource(getEmlInputStream());
    return src;
  }

  public List<String> getExtensionOrder() {
    return extensionOrder;
  }

  public Map<String, Extension> getExtensions() {
    return extensions;
  }

  public Map<String, List<ArchiveField>> getFields() {
    return fields;
  }

  public Map<String, List<ExtensionProperty>> getFieldsMissing() {
    return fieldsMissing;
  }

  public Map<String, List<ArchiveField>> getFieldsUnknown() {
    return fieldsUnknown;
  }

  public String getFileFileName() {
    return fileFileName;
  }

  public Date getLastUpdated() {
    return validation.getLastUpdate();
  }

  public String getMeta() {
    return meta;
  }

  public BasicMetadata getMetadata() {
    return metadata;
  }

  public Exception getMetadataException() {
    return metadataException;
  }

  public ArrayList<StackTraceElement> getMetadataStackTrace() {
    return metadataStackTrace;
  }

  public List<List<List<String>>> getRecords() {
    return records;
  }

  public Exception getRecordsException() {
    return recordsException;
  }

  public Map<String, List<String>> getRecordsHeader() {
    return recordsHeader;
  }

  public ArrayList<StackTraceElement> getRecordsStackTrace() {
    return recordsStackTrace;
  }

  public int getScanSize() {
    return scanSize;
  }

  public Exception getSchemaException() {
    return schemaException;
  }

  public List<StackTraceElement> getSchemaStackTrace() {
    return schemaStackTrace;
  }

  private void inspectArchiveFile(ArchiveFile af, boolean core) {
    String rowType = af.getRowType();
    String filename = af.getLocation();
    // in case the archive is only a file, this will be null, so we need to use the archive folder name instead!
    if(filename==null){
      filename=af.getArchive().getLocation().getName();
    }

    fields.put(rowType, new ArrayList<ArchiveField>());
    fieldsUnknown.put(rowType, new ArrayList<ArchiveField>());
    fieldsMissing.put(rowType, new ArrayList<ExtensionProperty>());

    // registered extension?
    Extension ext = extensionManager.get(rowType);

    Collection<ArchiveField> mapped = af.getFields().values();
    if (ext != null) {
      extensions.put(rowType, ext);
      // missing required fields?
      for (ExtensionProperty p : ext.getProperties()) {
        if (p.isRequired() && !af.hasTerm(p.getQualname())) {
          fieldsMissing.get(rowType).add(p);
          valid = false;
        }
      }
    }

    // known and unknown mapped fields
    for (ArchiveField f : mapped) {
      if (ext != null) {
        if (ext.hasProperty(f.getTerm())) {
          fields.get(rowType).add(f);
        } else {
          fieldsUnknown.get(rowType).add(f);
        }
      } else {
        fields.get(rowType).add(f);
      }
    }

    // test data file if not previously done already (same file can be mapped more than once)
    Map<Integer, String[]> afBrokenLines = new HashMap<Integer, String[]>();
    Set<String> afMissingIds = new CompactHashSet<String>();

    try {
      CSVReader reader = af.getCSVReader();
      int idColumn = -1;
      if (af.getId()!=null && af.getId().getIndex()!=null){
        idColumn= af.getId().getIndex();
      }
      int rowSize = -1;
      if (reader.headerRows>0){
        rowSize = reader.header.length;
      }
      int acceptedUsageIdx = -1;
      if (af.hasTerm(DwcTerm.acceptedNameUsageID)) {
        acceptedUsageIdx = af.getField(DwcTerm.acceptedNameUsageID).getIndex();
      }
      while (reader.hasNext()){
        String[] row = reader.next();
        if (rowSize < 0) {
          rowSize = row.length;
        } else if (afBrokenLines.size() < MAX_RECORDS_REPORTED && row.length!=rowSize) {
          afBrokenLines.put(reader.currLineNumber(), row);
        }
        // check all columns for verbatim NULL strings
        for (String val : row){
          if (NULL_REPL.matcher(val).find()){
            nullValues.adjustOrPutValue(val, 1, 1);
          }
        }
        // core id?
        if (idColumn>=0 && row.length > idColumn){
          String coreID = StringUtils.trimToNull(row[idColumn]);
          if (core) {
            // if its a taxon, is it a synonym?
            byte synonym = 0;
            if (acceptedUsageIdx > -1){
              String acceptedID = StringUtils.trimToNull(row[acceptedUsageIdx]);
              if (acceptedID != null && !acceptedID.equals(coreID)){
                 synonym = 1;
              }
            }
            // check uniqueness of taxonID
            if (coreIds.size() < MAX_IDS_STORED) {
              if (coreIds.containsKey(coreID) && nonUniqueId.size()< MAX_RECORDS_REPORTED){
                nonUniqueId.add(coreID);
              } else{
                coreIds.put(coreID, synonym);
              }
            }else{
              tooManyCoreIds=true;
            }
          } else {
            if (!tooManyCoreIds && afMissingIds.size() < MAX_RECORDS_REPORTED && !coreIds.containsKey(coreID)) {
               // we know about all core ids - make sure the extension one exists
              afMissingIds.add(coreID);
            }
          }
        }
      }
      // report empty lines
      emptyLines = reader.getEmptyLines();
      fileLines.put(filename, reader.currLineNumber());
      fileColumns.put(filename, rowSize);

      // potential second pass to verify foreign keys in the core
      if (core && !tooManyCoreIds) {
        Set<String> missingAcceptedUsageIDs = new HashSet<String>();
        this.brokenRefIntegrity.put(DwcTerm.acceptedNameUsageID.simpleName(), missingAcceptedUsageIDs);

        int parentUsageIdx = -1;
        Set<String> missingParentUsageIDs = new HashSet<String>();
        if (af.hasTerm(DwcTerm.parentNameUsageID)) {
          parentUsageIdx = af.getField(DwcTerm.parentNameUsageID).getIndex();
          this.brokenRefIntegrity.put(DwcTerm.parentNameUsageID.simpleName(), missingParentUsageIDs);
        }

        int originalNameIdx = -1;
        Set<String> missingOriginalUsageIDs = new HashSet<String>();
        if (af.hasTerm(DwcTerm.originalNameUsageID)) {
          originalNameIdx = af.getField(DwcTerm.originalNameUsageID).getIndex();
          this.brokenRefIntegrity.put(DwcTerm.originalNameUsageID.simpleName(), missingOriginalUsageIDs);
        }

        // only check file again if foreign key terms are mapped
        if (acceptedUsageIdx >= 0 || parentUsageIdx >= 0 || originalNameIdx >= 0){

          if (acceptedUsageIdx >= 0){
            acceptedSynonyms = new CompactHashSet<String>();
          }
          if (parentUsageIdx >= 0){
            parentSynonyms = new CompactHashSet<String>();
          }
          reader.close();
          reader = af.getCSVReader();
          while (reader.hasNext()) {
            String[] row = reader.next();
            String coreID = StringUtils.trimToNull(row[idColumn]);
            // check foreign key terms
            if (acceptedUsageIdx >= 0 && row.length > acceptedUsageIdx) {
              // it is allowed to concat multiple ids with PIPEs - split them
              String acceptedIds = StringUtils.trimToNull(row[acceptedUsageIdx]);
              if (acceptedIds!=null){
                for (String accId : StringUtils.split(acceptedIds,'|')){
                  if (missingAcceptedUsageIDs.size() < MAX_RECORDS_REPORTED && !coreIds.containsKey(accId)) {
                    missingAcceptedUsageIDs.add(accId);
                  }
                  // is the referenced accepted record a synonym?
                  if (acceptedSynonyms.size() < MAX_RECORDS_REPORTED && coreIds.containsKey(accId) && coreIds.get(accId) != 0){
                    acceptedSynonyms.add(coreID);
                  }
                }
              }
            }
            if (parentUsageIdx >= 0 && row.length > parentUsageIdx) {
              String parentID = row[parentUsageIdx];
              if (!StringUtils.isBlank(parentID)){
                if (missingParentUsageIDs.size() < MAX_RECORDS_REPORTED && !coreIds.containsKey(parentID)) {
                  missingParentUsageIDs.add(row[parentUsageIdx]);
                }
                // is the referenced parent record a synonym?
                if (parentSynonyms.size() < MAX_RECORDS_REPORTED && coreIds.containsKey(parentID) && coreIds.get(parentID) != 0){
                  parentSynonyms.add(coreID);
                }
              }
            }
            if (originalNameIdx >= 0 && row.length > originalNameIdx) {
              if (!StringUtils.isBlank(row[originalNameIdx]) && missingOriginalUsageIDs.size() < MAX_RECORDS_REPORTED && !coreIds.containsKey(row[originalNameIdx])) {
                missingOriginalUsageIDs.add(row[originalNameIdx]);
              }
            }
          }
        }
      }
    } catch (IOException e1) {
      valid = false;

    } finally {
      if (filename!=null){
        brokenLines.put(filename, afBrokenLines);
        missingIds.put(filename, afMissingIds);
      }
      // is this data file valid?
      if (!nonUniqueId.isEmpty()){
        valid = false;
      }
      if (!afBrokenLines.isEmpty()) {
        valid = false;
      }
      if (!afMissingIds.isEmpty()) {
        valid = false;
      }
      for (Set<String> integr : this.brokenRefIntegrity.values()){
        if (!integr.isEmpty()) {
          valid = false;
        }
      }
    }

  }

  public Map<String, Map<Integer, String[]>> getBrokenLines() {
    return brokenLines;
  }

  public Map<String, Set<String>> getMissingIds() {
    return missingIds;
  }

  public Map<String, Integer> getFileLines() {
    return fileLines;
  }

  public Set<String> getNonUniqueId() {
    return nonUniqueId;
  }

  public Set<Integer> getEmptyLines() {
    return emptyLines;
  }

  private List<String> interpretRecord(List<ConceptTerm> concepts, Record rec, boolean isCore, int rowSize) {
    List<String> row = new ArrayList<String>();
    if (isCore) {
      row.add(rec.id());
    } else {
      String name = StringUtils.substringAfterLast(rec.rowType(), "/");
      row.add(name);
    }
    for (ConceptTerm t : concepts) {
      row.add(rec.value(t));
    }
    // make sure all rows have the same width
    while (row.size() < rowSize) {
      row.add("");
    }
    return row;
  }

  public boolean isEmlSchemaValidated() {
    return emlSchemaValidated;
  }

  public boolean isGbifSchemaValidated() {
    return gbifSchemaValidated;
  }

  public boolean isMetaExists() {
    return metaExists;
  }

  public boolean isMetaOnly() {
    return metaOnly;
  }

  private void setDwcaException(Exception e) {
    valid = false;
    dwcaException = e;
    for (StackTraceElement el : e.getStackTrace()) {
      if (el.getClassName().equalsIgnoreCase(this.getClass().getName())) {
        dwcaStackTrace.add(el);
        break;
      }
      dwcaStackTrace.add(el);
    }
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }

  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }

  public void setMeta(String meta) {
    this.meta = meta;
  }

  public String getArchiveUrl() {
    return archiveUrl;
  }

  public void setArchiveUrl(String archiveUrl) {
    this.archiveUrl = archiveUrl;
  }

  private void setMetadataException(Exception e) {
    metadataException = e;
    for (StackTraceElement el : e.getStackTrace()) {
      if (el.getClassName().equalsIgnoreCase(this.getClass().getName())) {
        metadataStackTrace.add(el);
        break;
      }
      metadataStackTrace.add(el);
    }
  }

  private void setRecords() {
    try {
      // prepare ordered headers
      Map<String, List<ConceptTerm>> recordsHeaderFull = new HashMap<String, List<ConceptTerm>>();
      List<ConceptTerm> terms = new ArrayList<ConceptTerm>();
      recordsHeaderFull.put(archive.getCore().getRowType(), terms);
      for (ConceptTerm t : archive.getCore().getFields().keySet()) {
        terms.add(t);
      }
      int maxRecordWidth = terms.size();
      for (ArchiveFile af : archive.getExtensions()) {
        terms = new ArrayList<ConceptTerm>();
        recordsHeaderFull.put(af.getRowType(), terms);
        for (ConceptTerm t : af.getFields().keySet()) {
          terms.add(t);
        }
        if (terms.size() > maxRecordWidth) {
          maxRecordWidth = terms.size();
        }
      }
      // finally loop thru data
      ClosableIterator<StarRecord> iter = archive.iterator();
      int i = 0;
      while (iter.hasNext() && i < scanSize) {
        StarRecord rec = iter.next();
        List<List<String>> interpretedRecord = new ArrayList<List<String>>();
        records.add(interpretedRecord);
        // first the core
        interpretedRecord.add(interpretRecord(recordsHeaderFull.get(rec.core().rowType()), rec.core(), true,
            maxRecordWidth + 1));
        for (Record r : rec) {
          interpretedRecord.add(interpretRecord(recordsHeaderFull.get(r.rowType()), r, false, maxRecordWidth));
        }
        i++;
      }
      // finally use only simple extension names for headers:
      String coreName = StringUtils.substringAfterLast(archive.getCore().getRowType(), "/");
      recordsHeader.put(coreName, null);
      List<String> extensionNames = new ArrayList<String>();
      for (String rt : recordsHeaderFull.keySet()) {
        String name = StringUtils.substringAfterLast(rt, "/");
        List<String> concepts = new ArrayList<String>();
        for (ConceptTerm ct : recordsHeaderFull.get(rt)) {
          concepts.add(ct.simpleName());
        }
        while (concepts.size() < maxRecordWidth) {
          concepts.add("");
        }
        recordsHeader.put(name, concepts);
        extensionNames.add(name);
      }
      extensionOrder.add(coreName);
      extensionNames.remove(coreName);
      Collections.sort(extensionNames);
      extensionOrder.addAll(extensionNames);
    } catch (Exception e) {
      setRecordsException(e);
    }
  }

  private void setRecordsException(Exception e) {
    recordsException = e;
    for (StackTraceElement el : e.getStackTrace()) {
      if (el.getClassName().equalsIgnoreCase(this.getClass().getName())) {
        recordsStackTrace.add(el);
        break;
      }
      recordsStackTrace.add(el);
    }
  }

  private void setSchemaException(Exception e) {
    valid = false;
    schemaException = e;
    schemaStackTrace = new ArrayList<StackTraceElement>();
    for (StackTraceElement el : e.getStackTrace()) {
      if (el.getClassName().equalsIgnoreCase(this.getClass().getName())) {
        schemaStackTrace.add(el);
        break;
      }
      schemaStackTrace.add(el);
    }
  }

  /**
   *
   */
  private void validateAgainstSchema(File metaFile) {
    if (metaFile != null) {
      // perform validation:
      log.info("Validating meta.xml ...");
      try {
        validation.getMetaValidator().validate(new StreamSource(metaFile));
        log.info("XML Schema validation success.");
      } catch (Exception e) {
        setSchemaException(e);
      }
    } else {
      metaExists = false;
    }
  }

  /**
   * @param dwcaFolder
   */
  private void validateArchive(File dwcaFolder) {
    if (dwcaFolder==null){
      return;
    }

    log.info("Inspecting uploaded dwc archive");
    try {
      archive = ArchiveFactory.openArchive(dwcaFolder);

      // inspect dwca folder files
      if (dwcaFolder.isDirectory()) {
        if (archive!=null && archive.getCore()!=null){
          coreFile = archive.getCore().getLocation();
        }
        dwcaFiles = new HashSet<String>(Arrays.asList(dwcaFolder.list(HiddenFileFilter.VISIBLE)));
        dwcaFiles.remove("meta.xml");
        if (archive.getMetadataLocation() != null) {
          dwcaFiles.remove(new File(archive.getMetadataLocation()).getName());
        }
      } else {
        coreFile = dwcaFolder.getName();
        dwcaFiles.add(coreFile);
      }

      // inspect archive files
      ArchiveFile af = archive.getCore();
      inspectArchiveFile(af, true);
      for (ArchiveFile ext : archive.getExtensions()) {
        inspectArchiveFile(ext, false);
      }

      // read records
      if (!metaOnly) {
        setRecords();
      }
    } catch (Exception e) {
      setDwcaException(e);
    }

    // read metadata
    try {
      metadata = archive.getMetadata();
    } catch (Exception e) {
      setMetadataException(e);
    }
  }

  /**
   *
   */
  private void validateEml() {
    // perform validation. EML first
    log.info("Validating against EML ...");

    // try gbif profile
    log.info("Validating against GBIF profile ...");
    try {
      validation.getGbifProfileValidator().validate(getEmlSource());
      gbifSchemaValidated = true;
      log.info("GBIF Profile Schema validation success.");

      // try against the official eml schema
      try {
        validation.getEmlValidator().validate(getEmlSource());
        emlSchemaValidated = true;
        log.info("EML Schema validation success.");
      } catch (Exception e) {
        setMetadataException(e);
      }

    } catch (Exception e) {
      setMetadataException(e);
    }

    // try to parse EML doc with dwca reader
    try {
      eml = EmlFactory.build(getEmlInputStream());
    } catch (Exception e) {
      log.info("Cant parse eml document with eml factory");
      emlException = e;
    }

  }

  public boolean isValid() {
    return valid;
  }

  public String getReportId() {
    return reportId;
  }

  public void setReportId(String reportId) {
    this.reportId = reportId;
  }

  public boolean isOnline() {
    return online;
  }

  public Date getNow() {
    return now;
  }

  public String getReportUrl() {
    return reportUrl;
  }

  public String getOfflineReason() {
    return offlineReason;
  }

  public String getCoreFile() {
    return coreFile;
  }

  public String getIfModifiedSince() {
    return ifModifiedSince;
  }

  public void setIfModifiedSince(String ifModifiedSince) {
    this.ifModifiedSince = ifModifiedSince;
  }

  public StatusLine getStatus() {
    return status;
  }

  public Set<String> getParentSynonyms() {
    return parentSynonyms;
  }

  public Set<String> getAcceptedSynonyms() {
    return acceptedSynonyms;
  }

  public TObjectLongHashMap<String> getNullValues() {
    return nullValues;
  }
}
