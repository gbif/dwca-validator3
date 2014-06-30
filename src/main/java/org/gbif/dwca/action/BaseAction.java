/**
 * 
 */
package org.gbif.dwca.action;

import org.gbif.dwca.config.AppConfig;
import org.gbif.metadata.eml.ValidatorFactory;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base of all IPT actions This handles conditions such as menu items, a custom text provider, sessions, currently
 * logged in user
 * 
 * @author tim
 */
public class BaseAction extends ActionSupport implements Action, SessionAware, Preparable, ServletRequestAware {
  private static final long serialVersionUID = -2330991910834399442L;
  public static final String NOT_FOUND = "404";
  public static final String HOME = "home";
  protected boolean notFound = false;

  /**
   * Occassionally Struts2 complains with it's own logging which seems like a Struts2 issue
   */
  protected static Logger log = LoggerFactory.getLogger(BaseAction.class);

  protected Map<String, Object> session;
  @Inject
  protected AppConfig cfg;
  protected HttpServletRequest req;
  // a generic identifier for loading an object BEFORE the param interceptor sets values
  protected String id = null;

  public BaseAction() {

  }

  @Inject
  public BaseAction(AppConfig cfg) {
    super();
    this.cfg = cfg;
  }

  @Override
  public String execute() throws Exception {
    // if notFound was set to true during prepare() the supplied id parameter didnt exist - return a 404!
    if (notFound) {
      return NOT_FOUND;
    }
    return SUCCESS;
  }

  /**
   * Easy access to the configured application root for simple use in templates
   * 
   * @return
   */
  public String getBase() {
    return cfg.getBaseURL();
  }

  public String getBaseURL() {
    return cfg.getBaseURL();
  }

  public AppConfig getCfg() {
    return cfg;
  }

  public String getId() {
    return id;
  }

  public String getSchemaEmlGbifUrl() {
    return ValidatorFactory.EML_GBIF_PROFILE_SCHEMA_URL;
  }

  public String getSchemaEmlUrl() {
    return ValidatorFactory.EML_SCHEMA_URL;
  }

  protected boolean isHttpPost() {
    if (req.getMethod().equalsIgnoreCase("post")) {
      return true;
    }
    return false;
  }

  /**
   * Override this method if you need to load entities based on the id value before the PARAM interceptor is called. You
   * can also use this method to prepare a new, empty instance in case no id was provided. If the id parameter alone is
   * not sufficient to load your entities, you can access the request object directly like we do here and read any other
   * parameter you need to prepare the action for the param phase.
   */
  public void prepare() throws Exception {
    // see if an id was provided in the request.
    // we dont use the PARAM - PREPARE - PARAM interceptor stack
    // so we investigate the request object directly BEFORE the param interceptor is called
    // this allows us to load any existing instances that should be modified
    id = StringUtils.trimToNull(req.getParameter("id"));
  }

  public void setServletRequest(HttpServletRequest req) {
    this.req = req;
  }

  public void setSession(Map<String, Object> session) {
    this.session = session;
  }

}
