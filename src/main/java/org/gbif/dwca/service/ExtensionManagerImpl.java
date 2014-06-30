/**
 *
 */
package org.gbif.dwca.service;

import org.gbif.dwca.config.AppConfig;
import org.gbif.dwca.config.Constants;
import org.gbif.dwca.model.Extension;
import org.gbif.dwca.model.factory.ExtensionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tim
 */
@Singleton
public class ExtensionManagerImpl implements ExtensionManager {
  class UpdateExtensionsTask extends TimerTask {

    @Override
    public void run() {
      log.info("Start updating extensions from registry");
      updateFromRegistry();
    }

  }

  protected Logger log = LoggerFactory.getLogger(this.getClass());
  protected AppConfig cfg;
  private Map<String, Extension> extensionsByRowtype = new HashMap<String, Extension>();
  private ExtensionFactory factory;
  private HttpClient httpClient;
  private final String TAXON_KEYWORD = "dwc:taxon";
  private final String OCCURRENCE_KEYWORD = "dwc:occurrence";
  private final String OCCURRENCE_DWC = "http://rs.tdwg.org/dwc/terms/Occurrence";
  private final String SIMPLE_DWC = "http://rs.tdwg.org/dwc/xsd/simpledarwincore/SimpleDarwinRecord";
  private Date registryUpdate;
  private ObjectMapper mapper = new ObjectMapper();

  private final Timer timer = new Timer();

  @Inject
  public ExtensionManagerImpl(ExtensionFactory factory, HttpClient httpClient, AppConfig cfg) {
    super();
    this.factory = factory;
    this.httpClient = httpClient;
    this.cfg = cfg;
    // scheduled for every hour
    this.timer.scheduleAtFixedRate(new UpdateExtensionsTask(), new Date(), 1 * 60 * 60 * 1000);
  }

  private List<URL> discoverExtensions(boolean development) {
    List<URL> extensions = new ArrayList<URL>();

    // rely on the fact that AppConfig is already setup
    try {
      String url = cfg.getRegistryUrl();
      if (development) {
        url = cfg.getRegistryDevUrl();
      }
      url += "/registry/extensions.json";

      // get json
      log.info("Retrieving extensions from registry api: " + url);
      Map<String, Object> registryResponse = null;
      registryResponse = mapper.readValue(new URL(url), Map.class);
      List<Map<String, Object>> jsonExtensions = (List<Map<String, Object>>) registryResponse.get("extensions");
      for (Map<String, Object> ext : jsonExtensions) {
          try {
            extensions.add(new URL((String)ext.get("url")));
          } catch (Exception e) {
            log.error("Exception when listing extensions", e);
          }
        }
    log.info("Discovered {} extensions in the {} registry", extensions.size(), development ? "dev" : "production");
    } catch (MalformedURLException e) {
      log.error("MalformedURLException when discovering extensions", e);
    } catch (IOException e) {
      log.error("IOException when discovering extensions", e);
    }

    return extensions;
  }

  public Extension get(String rowType) {
    if (SIMPLE_DWC.equals(rowType)) {
      rowType = OCCURRENCE_DWC;
    }
    return extensionsByRowtype.get(rowType);
  }

  public Date getRegistryUpdate() {
    return registryUpdate;
  }

  public void install(URL url, boolean dev) {
    HttpGet get = new HttpGet(url.toString());
    HttpEntity entity = null;
    try {
      HttpResponse response = httpClient.execute(get);
      entity = response.getEntity();
      if (entity != null) {
        InputStream is = entity.getContent();
        Extension ext = factory.build(is, url, dev);
        if (ext != null && ext.getRowType() == null) {
          log.error("Extension {} lacking required rowType!", url);
        } else {
          // keep vocab in memory
          extensionsByRowtype.put(ext.getRowType(), ext);
          log.info("Successfully loaded {} extension {}", dev?"dev":"production", ext.getRowType());
        }
      }
    } catch (Exception e) {
      log.error("Error loading extension {}", url, e);

    } finally {
      if (entity != null) {
        try {
          EntityUtils.consume(entity);
        } catch (IOException e) {
          log.warn("Cannot consume extension http entity", e);
        }
      }
    }
  }

  public List<Extension> list() {
    return new ArrayList<Extension>(extensionsByRowtype.values());
  }

  public List<Extension> list(Extension core) {
    if (core != null && core.getRowType().equalsIgnoreCase(Constants.DWC_ROWTYPE_OCCURRENCE)) {
      return search(OCCURRENCE_KEYWORD);
    } else if (core != null && core.getRowType().equalsIgnoreCase(Constants.DWC_ROWTYPE_TAXON)) {
      return search(TAXON_KEYWORD);
    } else {
      return list();
    }
  }

  public List<Extension> listCore() {
    List<Extension> list = new ArrayList<Extension>();
    Extension e = get(Constants.DWC_ROWTYPE_OCCURRENCE);
    if (e != null) {
      list.add(e);
    }
    e = get(Constants.DWC_ROWTYPE_TAXON);
    if (e != null) {
      list.add(e);
    }
    return list;
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.dwca.service.ExtensionManager#map()
   */
  public Map<String, Extension> map() {
    return extensionsByRowtype;
  }

  public List<Extension> search(String keyword) {
    List<Extension> list = new ArrayList<Extension>();
    keyword = keyword.toLowerCase();
    for (Extension e : extensionsByRowtype.values()) {
      if (StringUtils.containsIgnoreCase(e.getSubject(), keyword)) {
        list.add(e);
      }
    }
    return list;
  }

  public int updateFromRegistry() {
    int counter = 0;
    registryUpdate = new Date();

    // development extensions
    List<URL> extensions = discoverExtensions(true);
    for (URL url : extensions) {
      log.info("Loading dev #{} extension {} ...", counter+1, url);
      if (url.getFile().endsWith("GermplasmAccession.xml")){
        log.info("GermplasmAccession.xml");
      }
      install(url, true);
      counter++;
    }

    // production extensions last, overwriting dev ones if needed
    extensions = discoverExtensions(false);
    for (URL url : extensions) {
      log.info("Loading production #{} extension {} ...", counter+1, url);
      install(url, false);
      counter++;
    }

    return counter;
  }
}
