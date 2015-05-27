package org.gbif.dwca.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import com.google.common.base.Throwables;
import com.google.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AppConfig {
  private static final String CLASSPATH_PROPFILE = "application.properties";
  private static final String REPORTS_DIR_KEY = "reports.dir";
  public static final String BASEURL = "baseURL";
  private Properties properties = new Properties();
  private Logger log = LoggerFactory.getLogger(this.getClass());
  private File reportsDir;

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

  public String getEmlSchema() {
    return getProperty("schema.eml");
  }

  public String getGbifSchema() {
    return getProperty("schema.gbif");
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public URI getGbifResourceSite() {
    return URI.create("http://rs.gbif.org");
  }

  public URI getProdExtensions() {
    return getGbifResourceSite().resolve("/extensions.json");
  }

  public URI getDevExtensions() {
    return getGbifResourceSite().resolve("/sandbox/extensions.json");
  }

  public String getVersion() {
    return properties.getProperty("version");
  }

  public File getReportsDir() {
    if (reportsDir == null) {
      reportsDir = new File(getProperty(REPORTS_DIR_KEY));
    }
    return reportsDir;
  }
  public void loadConfig() {
    try {
      InputStream configStream = AppConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_PROPFILE);
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
    // create reports folder if needed
    if (!getReportsDir().exists()) {
      try {
        FileUtils.forceMkdir(getReportsDir());
      } catch (IOException e) {
        log.error("Failed to create reports directory {}", getReportsDir().getAbsoluteFile());
        Throwables.propagate(e);
      }
    }
  }

}