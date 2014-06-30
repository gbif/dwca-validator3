/**
 * 
 */
package org.gbif.dwca.service;

import org.gbif.dwca.model.Vocabulary;

import com.google.inject.ImplementedBy;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * This interface details ALL methods associated with the vocabularies within the IPT.
 * 
 * @author tim
 */
@ImplementedBy(VocabulariesManagerImpl.class)
public interface VocabulariesManager {

  /**
   * Retrieve vocabulary by its unique global URI identifier from installed vocabularies.
   * 
   * @param uri unique URI identifying the vocabulary as given in the vocabulary definition
   * @return the installed vocabulary or null if not found
   */
  public Vocabulary get(String uri);

  /**
   * Returns the parsed vocabulary located at the given URL. If downloaded already it will return the cached copy or
   * otherwise download it from the URL.
   * 
   * @param url the resolvable URL that locates the xml vocabulary definition
   * @return
   */
  public Vocabulary get(URL url);

  /**
   * Returns a regular map than can be used to populate html select drop downs with
   * keys=vocabulary concept identifiers and values=preferred term for the given language.
   * Defaults to english if no term for the requested language exists.
   * 
   * @param uri the identifier for the vocabulary
   * @param lang a 2 character iso language code, e.g. DE
   * @return
   */
  public Map<String, String> getI18nVocab(String uri, String lang);

  /**
   * Lists all locally known vocabularies
   * 
   * @return
   */
  public List<Vocabulary> list();

}
