/**
 *
 */
package org.gbif.dwca.service;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.config.AppConfig;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
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
  private Map<Term, Extension> extensionsByRowtype = new HashMap<Term, Extension>();
  private ExtensionFactory factory;
  private HttpClient httpClient;
  private TermFactory TF = TermFactory.instance();
  private final String TAXON_KEYWORD = "dwc:taxon";
  private final String OCCURRENCE_KEYWORD = "dwc:occurrence";
  private final Term OCCURRENCE_DWC = DwcTerm.Occurrence;
  private final Term SIMPLE_DWC = TF.findTerm("http://rs.tdwg.org/dwc/xsd/simpledarwincore/SimpleDarwinRecord");
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
      URL url;
      if (development) {
        url = cfg.getDevExtensions().toURL();
      } else {
        url = cfg.getProdExtensions().toURL();
      }

      // get json
      log.info("Retrieving extensions from " + url);
      Map<String, Object> registryResponse = mapper.readValue(url, Map.class);
      List<Map<String, Object>> jsonExtensions = (List<Map<String, Object>>) registryResponse.get("extensions");
      for (Map<String, Object> ext : jsonExtensions) {
        try {
          extensions.add(new URL((String)ext.get("url")));
        } catch (Exception e) {
          log.error("Exception when listing extensions", e);
        }
      }
    log.info("Discovered {} extensions in {}", extensions.size(), development ? "sandbox" : "production");
    } catch (MalformedURLException e) {
      log.error("MalformedURLException when discovering extensions", e);
    } catch (IOException e) {
      log.error("IOException when discovering extensions", e);
    }

    return extensions;
  }

  @Override
  public Extension get(Term rowType) {
    if (SIMPLE_DWC.equals(rowType)) {
      rowType = OCCURRENCE_DWC;
    }
    return extensionsByRowtype.get(rowType);
  }

  @Override
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

  @Override
  public List<Extension> list() {
    return new ArrayList<Extension>(extensionsByRowtype.values());
  }

  @Override
  public List<Extension> list(Extension core) {
    if (core != null && core.getRowType() == DwcTerm.Occurrence) {
      return search(OCCURRENCE_KEYWORD);
    } else if (core != null && core.getRowType() == DwcTerm.Taxon) {
      return search(TAXON_KEYWORD);
    } else {
      return list();
    }
  }

  @Override
  public List<Extension> listCore() {
    List<Extension> list = new ArrayList<Extension>();
    Extension e = get(DwcTerm.Occurrence);
    if (e != null) {
      list.add(e);
    }
    e = get(DwcTerm.Taxon);
    if (e != null) {
      list.add(e);
    }
    return list;
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.dwca.service.ExtensionManager#map()
   */
  @Override
  public Map<Term, Extension> map() {
    return extensionsByRowtype;
  }

  @Override
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

  @Override
  public int updateFromRegistry() {
    int counter = 0;
    registryUpdate = new Date();

    // development extensions
    List<URL> extensions = discoverExtensions(true);
    for (URL url : extensions) {
      log.info("Loading dev #{} extension {} ...", counter+1, url);
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
