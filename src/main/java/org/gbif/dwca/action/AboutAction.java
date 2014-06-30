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

package org.gbif.dwca.action;

import org.gbif.dwca.service.ValidationService;

import com.google.inject.Inject;

import java.util.Date;

/**
 * @author markus
 * 
 */
public class AboutAction extends BaseAction {

  private String schemaMetaUrl;
  private String registryUrl;
  private String registryDevUrl;
  @Inject
  private ValidationService validation;

  @Override
  public String execute() throws Exception {
    schemaMetaUrl = cfg.getMetaSchema();
    registryUrl = cfg.getRegistryUrl();
    registryDevUrl = cfg.getRegistryDevUrl();
    return super.execute();
  }

  public Date getLastSchemaUpdate() {
    return validation.getLastUpdate();
  }

  public String getRegistryDevUrl() {
    return registryDevUrl;
  }

  public String getRegistryUrl() {
    return registryUrl;
  }

  public String getSchemaMetaUrl() {
    return schemaMetaUrl;
  }

}
