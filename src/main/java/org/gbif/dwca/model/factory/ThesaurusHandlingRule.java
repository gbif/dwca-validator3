/*
 * Copyright 2009 GBIF. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.gbif.dwca.model.factory;

import org.gbif.dwca.model.ExtensionProperty;
import org.gbif.dwca.model.Vocabulary;
import org.gbif.dwca.service.VocabulariesManager;

import java.net.URL;

import com.google.inject.Inject;
import org.apache.commons.digester.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * This will call the root of the stack to find the url2thesaurus, and then set the appropriate thesaurus on the
 * extension. Namespaces are completely ignored. The "thesaurus" attribute is searched for and if found, the thesaurus
 * is set if found.
 */
public class ThesaurusHandlingRule extends Rule {
  public static final String ATTRIBUTE_THESAURUS = "thesaurus";
  protected static Logger log = LoggerFactory.getLogger(ThesaurusHandlingRule.class);
  private VocabulariesManager vocabManager;

  @Inject
  public ThesaurusHandlingRule(VocabulariesManager vocabManager) {
    super();
    this.vocabManager = vocabManager;
  }

  @Override
  public void begin(String namespace, String name, Attributes attributes) throws Exception {

    for (int i = 0; i < attributes.getLength(); i++) {
      if (ThesaurusHandlingRule.ATTRIBUTE_THESAURUS.equals(attributes.getQName(i))) {
        Vocabulary tv = null;
        try {
          URL vocabURL = new URL(attributes.getValue(i));
          tv = vocabManager.get(vocabURL);
        } catch (Exception e) {
          log.error("Could not load vocabulary with location {}: {}", new Object[]{attributes.getValue(i), e.getMessage(), e});
        }

        if (tv != null) {
          Object extensionPropertyAsObject = getDigester().peek();
          if (extensionPropertyAsObject instanceof ExtensionProperty) {
            ExtensionProperty eProperty = (ExtensionProperty) extensionPropertyAsObject;
            eProperty.setVocabulary(tv);
            log.debug("Vocabulary with URI[{}] added to extension property", tv.getUri());
          }
        } else {
          log.warn("No vocabulary exists for the URL[{}]", attributes.getValue(i));
        }

        break; // since we found the attribute
      }
    }
  }
}
