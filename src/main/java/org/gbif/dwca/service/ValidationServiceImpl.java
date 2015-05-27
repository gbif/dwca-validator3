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

package org.gbif.dwca.service;

import org.gbif.dwca.config.AppConfig;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author markus
 * 
 */
@Singleton
public class ValidationServiceImpl implements ValidationService {
  private static final String SCHEMA_LANG = "http://www.w3.org/2001/XMLSchema";
  private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);

  class UpdateValidatorsTask extends TimerTask {
    @Override
    public void run() {
      updateSchemas();
    }
  }

  private AppConfig cfg;
  private Date lastUpdate;
  private Validator metaValidator;
  private Validator emlValidator;
  private Validator gbifProfileValidator;
  private final Timer timer = new Timer();

  @Inject
  public ValidationServiceImpl(AppConfig cfg) {
    super();
    this.cfg = cfg;
    // scheduled for every 24 hours
    this.timer.scheduleAtFixedRate(new UpdateValidatorsTask(), new Date(), 24 * 60 * 60 * 1000);
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.dwca.service.ValidationService#getEmlValidator()
   */
  @Override
  public Validator getEmlValidator() {
    if (emlValidator == null) {
      updateSchemas();
    }
    return emlValidator;
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.dwca.service.ValidationService#getGbifProfileValidator()
   */
  @Override
  public Validator getGbifProfileValidator() {
    if (gbifProfileValidator == null) {
      updateSchemas();
    }
    return gbifProfileValidator;
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.dwca.service.ValidationService#getLastUpdate()
   */
  @Override
  public Date getLastUpdate() {
    return lastUpdate;
  }

  /*
   * (non-Javadoc)
   * @see org.gbif.dwca.service.ValidationService#getMetaValidator()
   */
  @Override
  public Validator getMetaValidator() {
    if (metaValidator == null) {
      updateSchemas();
    }
    return metaValidator;
  }

  private void updateSchemas() {
    LOG.info("Updating validation schemas...");
    lastUpdate = new Date();

    try {
      // meta validator
      metaValidator = getValidator(cfg.getMetaSchema());

      // GBIF Profile
      gbifProfileValidator = getValidator(cfg.getGbifSchema());

      // EML Schema
      emlValidator = getValidator(cfg.getEmlSchema());

    } catch (Exception e) {
      Throwables.propagate(e);
    }
  }

  private static Validator getValidator(String url) throws IOException, SAXException {
    LOG.info("Loading xml schema from {}", url);
    // define the type of schema - we use W3C:
    // resolve validation driver:
    SchemaFactory factory = SchemaFactory.newInstance(SCHEMA_LANG);
    // create schema by reading it from gbif online resources:
    Schema schema = factory.newSchema(new StreamSource(url));
    return schema.newValidator();
  }
}
