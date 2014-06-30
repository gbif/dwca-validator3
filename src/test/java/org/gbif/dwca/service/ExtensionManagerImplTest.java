package org.gbif.dwca.service;

import org.gbif.dwca.config.GuiceModule;

import java.net.URL;
import javax.servlet.ServletContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;

@Ignore
public class ExtensionManagerImplTest {

  class ServletMockModule extends AbstractModule {

    @Provides
    ServletContext provideServletContext(){
      return mock(ServletContext.class);
    }

    @Override
    protected void configure() {
    }
  }

  @Test
  public void testInstall() throws Exception {
    Injector inj = Guice.createInjector(new GuiceModule(), new ServletMockModule());
    ExtensionManagerImpl em = (ExtensionManagerImpl) inj.getInstance(ExtensionManager.class);
    em.install(new URL("http://rs.gbif.org/sandbox/extension/germplasm/20120717/GermplasmAccession.xml"), true);
    System.out.print("GUTES ENDE");
  }
}
