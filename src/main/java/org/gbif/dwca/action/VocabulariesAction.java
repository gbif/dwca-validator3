package org.gbif.dwca.action;

import org.gbif.dwca.model.Vocabulary;
import org.gbif.dwca.service.VocabulariesManager;

import com.google.inject.Inject;

/**
 * The Action responsible for all user input relating to the vocabularies in use within the IPT
 * 
 */
public class VocabulariesAction extends BaseAction {
  private static final long serialVersionUID = 7277675384287096912L;
  @Inject
  private VocabulariesManager vocabManager;
  private Vocabulary vocabulary;

  @Override
  public String execute() throws Exception {
    if (id != null) {
      vocabulary = vocabManager.get(id);
      if (vocabulary == null) {
        return NOT_FOUND;
      }
    }
    return SUCCESS;
  }

  public Vocabulary getVocabulary() {
    return vocabulary;
  }

}
