/**
 * 
 */
package org.gbif.dwca.action;

import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.model.Extension;
import org.gbif.dwca.model.Vocabulary;
import org.gbif.dwca.service.ExtensionManager;
import org.gbif.dwca.service.VocabulariesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

/**
 * The Action responsible for all user input relating to the DarwinCore extension management.
 */
public class ExtensionsAction extends BaseAction {
  @Inject
  private ExtensionManager extensionManager;
  @Inject
  private VocabulariesManager vocabManager;
  private List<Extension> extensions = new ArrayList<Extension>();
  private List<Extension> devExtensions = new ArrayList<Extension>();
  private Extension extension;
  private int count = 0;
  private Date vocabsLastUpdated = null;

  @Override
  public String execute() {
    if (id != null) {
      extension = extensionManager.get(TermFactory.instance().findTerm(id));
      if (extension != null) {
        return SUCCESS;
      }
    }
    return NOT_FOUND;
  }

  public String reload() {
    LOG.info("Force extension reload ...");
    extensionManager.getRegistryUpdate();
    return list();
  }

  public int getCount() {
    return count;
  }

  public List<Extension> getDevExtensions() {
    return devExtensions;
  }

  public Extension getExtension() {
    return extension;
  }

  public List<Extension> getExtensions() {
    return extensions;
  }

  public Date getLastUpdatedExtensions() {
    return extensionManager.getRegistryUpdate();
  }

  public Date getLastUpdatedVocabularies() {
    return vocabsLastUpdated;
  }

  public String list() {
    List<Extension> exts = extensionManager.list();
    Collections.sort(exts);
    for (Extension ext : exts) {
      if (ext.isDev()) {
        devExtensions.add(ext);
      } else {
        extensions.add(ext);
      }
    }
    Collection<Vocabulary> vocabs = vocabManager.list();
    count = vocabs.size();
    // find latest update data of any of all vocabularies
    for (Vocabulary v : vocabs) {
      if (vocabsLastUpdated == null || vocabsLastUpdated.before(v.getLastUpdate())) {
        vocabsLastUpdated = v.getLastUpdate();
      }
    }
    return SUCCESS;
  }

  public String update() {
    count = extensionManager.updateFromRegistry();
    return SUCCESS;
  }
}
