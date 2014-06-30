/**
 * 
 */
package org.gbif.dwca.service;

import org.gbif.dwca.model.Extension;

import com.google.inject.ImplementedBy;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This interface details ALL methods associated with the DwC extensions.
 * 
 * @author tim
 */
/**
 * @author markus
 */
@ImplementedBy(ExtensionManagerImpl.class)
public interface ExtensionManager {

  /**
   * Get a locally installed extension by its rowType
   * 
   * @param rowType
   * @return extension for that rowtype or null if not installed
   */
  public Extension get(String rowType);

  /**
   * @return the date extensions were last updated from the registry
   */
  public Date getRegistryUpdate();

  /**
   * List all installed extensions
   * 
   * @return list of installed IPT extensions
   */
  public List<Extension> list();

  /**
   * List all available extensions available for the given core
   * 
   * @param core extension
   * @return
   */
  public List<Extension> list(Extension core);

  /**
   * List only the available core extensions
   * 
   * @return
   */
  public List<Extension> listCore();

  public Map<String, Extension> map();

  /**
   * List all available extensions matching a registered keyword
   * 
   * @param keyword to filter by, e.g. dwc:Taxon for all taxonomic extensions
   * @return
   */
  public List<Extension> search(String keyword);

  /**
   * Load all extensions from the GBIF production and development registry
   * 
   * @return number of extensions that have been loaded successfully
   */
  public int updateFromRegistry();

}
