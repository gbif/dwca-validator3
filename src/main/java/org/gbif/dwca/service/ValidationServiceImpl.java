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
import org.gbif.metadata.eml.ValidatorFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 * @author markus
 * 
 */
@Singleton
public class ValidationServiceImpl implements ValidationService {
  class UpdateValidatorsTask extends TimerTask {
    @Override
    public void run() {
      updateSchemas();
    }
  }

  private Logger log = LoggerFactory.getLogger(this.getClass());
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
    // scheduled for every 12 hours
    this.timer.scheduleAtFixedRate(new UpdateValidatorsTask(), new Date(), 12 * 60 * 60 * 1000);
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
    log.info("Updating validation schemas...");
    lastUpdate = new Date();

    // meta validator
    String schemaUrl = cfg.getMetaSchema();
    try {
      // define the type of schema - we use W3C:
      String schemaLang = "http://www.w3.org/2001/XMLSchema";
      // get validation driver:
      SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
      // create schema by reading it from an URL:
      log.debug("Loading dwc-a xml schema from " + schemaUrl + " ...");
      Schema schema = factory.newSchema(new URL(schemaUrl));
      metaValidator = schema.newValidator();
      log.debug("Dwc-A descriptor schema validator updated");
    } catch (MalformedURLException e) {
      log.error("Cannot load dwc-a xml schema from " + schemaUrl, e);
    } catch (SAXException e) {
      log.error("Cannot parse dwc-a xml schema from " + schemaUrl, e);
    } catch (Exception e) {
      log.error("Unknown error loading dwc-a xml schema from " + schemaUrl, e);
    }

    // GBIF Profile
    try {
      gbifProfileValidator = ValidatorFactory.getGbifValidator();
      log.debug("EML GBIF Profile schema updated");
    } catch (MalformedURLException e) {
      log.error("Cannot load gbif profile xml schema", e);
    } catch (SAXException e) {
      log.error("Cannot parse gbif profile schema", e);
    } catch (Exception e) {
      log.error("Unknown error loading gbif profile schema", e);
    }

    // EML Schema
    try {
      emlValidator = ValidatorFactory.getEmlValidator();
      log.debug("EML schema updated");
    } catch (MalformedURLException e) {
      log.error("Cannot parse eml schema", e);
    } catch (SAXException e) {
      log.error("Cannot parse eml schema", e);
    } catch (Exception e) {
      log.error("Unknown error loading eml schema", e);
    }

  }
}
