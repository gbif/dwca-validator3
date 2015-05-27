package org.gbif.dwca.service;

import org.gbif.dwca.config.AppConfig;

import junit.framework.TestCase;
import org.junit.Test;

public class ValidationServiceImplTest extends TestCase {

  @Test
  public void testGetMetaValidator() throws Exception {
    AppConfig cfg = new AppConfig();
    cfg.loadConfig();
    ValidationServiceImpl vs = new ValidationServiceImpl(cfg);
    vs.getMetaValidator();
  }
}