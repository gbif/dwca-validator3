/**
 *
 */
package org.gbif.dwca.service;

import org.gbif.dwca.config.AppConfig;
import org.gbif.dwca.model.Vocabulary;
import org.gbif.dwca.model.VocabularyConcept;
import org.gbif.dwca.model.VocabularyTerm;
import org.gbif.dwca.model.factory.VocabularyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for all vocabulary related methods. Keeps an internal map of locally existing and parsed vocabularies which
 * is keyed on a normed filename derived from a vocabularies URL. We use this derived filename instead of the proper URL
 * as we do not persist any additional data than the extension file itself - which doesnt have its own URL embedded.
 */
@Singleton
public class VocabulariesManagerImpl implements VocabulariesManager {
  protected Logger log = LoggerFactory.getLogger(this.getClass());
  protected AppConfig cfg;

  private Map<String, Vocabulary> vocabularies = Maps.newHashMap();
  private Map<String, String> uri2url = Maps.newHashMap();
  private VocabularyFactory vocabFactory;
  private HttpClient httpClient;

  /**
   *
   */
  @Inject
  public VocabulariesManagerImpl(VocabularyFactory vocabFactory, HttpClient httpClient, AppConfig cfg) {
    super();
    this.vocabFactory = vocabFactory;
    this.httpClient = httpClient;
    this.cfg = cfg;
  }

  private boolean addToCache(Vocabulary v, String url) {
    if (url == null) {
      log.error("Cannot add vocabulary {} to cache without a valid URL", v.getUri());
      return false;
    }
    uri2url.put(v.getUri().toLowerCase(), url);
    // keep vocab in local lookup
    if (vocabularies.containsKey(url)) {
      log.warn("Vocabulary URI {} exists already - overwriting with new vocabulary from {}",v.getUri(), url);
    }
    vocabularies.put(url, v);
    return true;
  }

  public Vocabulary get(String uri) {
    if (uri == null) {
      return null;
    }
    String url = uri2url.get(uri.toLowerCase());
    return vocabularies.get(url);
  }

  public Vocabulary get(URL url) {
    String urlString = url.toString();
    if (!vocabularies.containsKey(urlString)) {
      install(urlString);
    }
    return vocabularies.get(urlString);
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.ipt.service.admin.VocabulariesManager#getI18nVocab(java.lang.String, java.lang.String)
   */
  public Map<String, String> getI18nVocab(String uri, String lang) {
    Map<String, String> map = new HashMap<String, String>();
    Vocabulary v = get(uri);
    if (v != null) {
      for (VocabularyConcept c : v.getConcepts()) {
        VocabularyTerm t = c.getPreferredTerm(lang);
        map.put(c.getIdentifier(), t == null ? c.getIdentifier() : t.getTitle());
      }
    }
    if (map.isEmpty()) {
      log.debug("Empty i18n map for vocabulary " + uri + " and language " + lang);
    }
    return map;
  }

  /**
   * Downloads vocabulary into local file for subsequent IPT startups
   * and adds the vocab to the internal cache.
   * Downloads use a conditional GET, i.e. only download the vocabulary files if the content has been changed since the
   * last download.
   * lastModified dates are taken from the filesystem.
   *
   * @param url
   * @return
   * @throws IOException
   */
  private void install(String url) {
    if (url != null) {
      // parse vocabulary file
      HttpGet get = new HttpGet(url);
      HttpEntity entity = null;
      try {
        HttpResponse response = httpClient.execute(get);
        entity = response.getEntity();
        if (entity != null) {
          InputStream is = entity.getContent();
          Vocabulary v = vocabFactory.build(is);
          EntityUtils.consume(entity);
          v.setLastUpdate(new Date());
          log.info("Successfully loaded Vocabulary: " + v.getTitle());
          addToCache(v, url);
        }

      } catch (ParserConfigurationException e) {
        log.error("ParserConfigurationException: {}", e.getMessage());

      } catch (Exception e) {
        log.error("Failed to install vocabulary {} : {}", url, e.getMessage());
      } finally {
        if (entity != null) {
          try {
            EntityUtils.consume(entity);
          } catch (IOException e) {
            log.warn("Cannot consume vocabulary http entity", e);
          }
        }
      }
    } else {
      log.warn("Ignore null vocabulary {}", url);
    }

  }

  public List<Vocabulary> list() {
    return new ArrayList<Vocabulary>(vocabularies.values());
  }

}
