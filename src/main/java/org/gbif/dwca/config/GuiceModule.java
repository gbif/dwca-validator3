package org.gbif.dwca.config;

import org.gbif.dwca.model.factory.ExtensionFactory;
import org.gbif.dwca.model.factory.ThesaurusHandlingRule;
import org.gbif.dwca.model.factory.VocabularyFactory;
import org.gbif.dwca.utils.InputStreamUtils;
import org.gbif.utils.HttpUtil;

import javax.servlet.ServletContext;
import javax.xml.parsers.SAXParserFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A guice module containing wiring used for both test and production.
 *
 * @author markus
 *
 */
public class GuiceModule extends AbstractModule {
  protected Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  protected void configure() {
    // singletons
    bind(InputStreamUtils.class).in(Scopes.SINGLETON);
    bind(ExtensionFactory.class).in(Scopes.SINGLETON);
    bind(VocabularyFactory.class).in(Scopes.SINGLETON);

    // prototypes
    bind(ThesaurusHandlingRule.class).in(Scopes.NO_SCOPE);
  }

  @Provides
  @Singleton
  public AppConfig provideAppConfig() {
    // rely on the fact that AppConfig is already setup
    AppConfig cfg = new AppConfig();
    cfg.loadConfig();
    return cfg;
  }

  @Provides
  @Singleton
  @Inject
  public DefaultHttpClient provideHttpClient() {
    // one minute timeout
    return HttpUtil.newMultithreadedClient(60000, 20, 5);
  }

  @Provides
  @Singleton
  @Inject
  public HttpClient provideHttpClient(DefaultHttpClient client) {
    return client;
  }

  @Provides
  @Singleton
  @Inject
  public HttpUtil provideHttpUtil(DefaultHttpClient client) {
    return new HttpUtil(client);
  }

  @Provides
  @Inject
  @Singleton
  public SAXParserFactory provideNsAwareSaxParserFactory() {
    SAXParserFactory saxf = null;
    try {
      saxf = SAXParserFactory.newInstance();
      saxf.setValidating(false);
      saxf.setNamespaceAware(true);
    } catch (Exception e) {
      log.error("Cant create namespace aware SAX Parser Factory: " + e.getMessage(), e);
    }
    return saxf;
  }

  @Provides
  @Singleton
  @Inject
  public Configuration provideFreemarker(ServletContext servletContext) {
    Configuration fm = new Configuration();
    // load templates from classpath by prefixing /templates
    TemplateLoader tl = new WebappTemplateLoader(servletContext);
    fm.setDefaultEncoding("utf8");
    fm.setTemplateLoader(tl);

    return fm;
  }
}
