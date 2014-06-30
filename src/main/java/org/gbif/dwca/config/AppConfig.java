package org.gbif.dwca.config;

import org.gbif.dwca.service.InvalidConfigException;
import org.gbif.dwca.utils.InputStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AppConfig {
  private static final String CLASSPATH_PROPFILE = "application.properties";
  public static final String BASEURL = "baseURL";
  private Properties properties = new Properties();
  private Logger log = LoggerFactory.getLogger(this.getClass());

  public AppConfig() {
  }

  public String getAnalyticsKey() {
    return getProperty("analytics.key");
  }

  public String getBaseURL() {
    String base = properties.getProperty(BASEURL);
    while (base != null && base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    return base;
  }

  public String getMetaSchema() {
    return getProperty("schema.meta");
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public String getRegistryDevUrl() {
    return getProperty("registrydev.url");
  }

  public String getRegistryUrl() {
    return getProperty("registry.url");
  }

  public String getVersion() {
    return properties.getProperty("version");
  }

  protected void loadConfig() throws InvalidConfigException {
    InputStreamUtils streamUtils = new InputStreamUtils();
    InputStream configStream = streamUtils.classpathStream(CLASSPATH_PROPFILE);
    try {
      this.properties = new Properties();
      if (configStream != null) {
        properties.load(configStream);
        log.debug("Loaded default configuration from application.properties in classpath");
      } else {
        log.error("Could not load default configuration from application.properties in classpath");
      }
    } catch (IOException e) {
      log.error("Failed to load the default application configuration from application.properties", e);
    }
  }

}