package org.gbif.dwca.service;

import org.gbif.dwca.config.AppConfig;

import junit.framework.TestCase;
import org.junit.Test;

public class ValidationServiceImplTest extends TestCase {

  @Test
  public void testGetMetaValidator() throws Exception {
    AppConfig cfg = new AppConfig();
    try {
      cfg.loadConfig();
    } catch (RuntimeException e) {
      // cant create report dir, expected...
    }
    ValidationServiceImpl vs = new ValidationServiceImpl(cfg);
    vs.getMetaValidator();
  }
}