package org.gbif.dwca.utils;

import org.gbif.dwca.action.BaseAction;

import org.slf4j.Logger;

public class ActionLogger {
  private final Logger log;
  private final BaseAction action;

  public ActionLogger(Logger log, BaseAction action) {
    super();
    this.log = log;
    this.action = action;
  }

  public void error(String message) {
    action.addActionError(action.getText(message));
    log.error(message);
  }

  public void error(String message, String[] args) {
    action.addActionError(action.getText(message, args));
    log.error(message);
  }

  public void error(String message, String[] args, Throwable t) {
    action.addActionError(action.getText(message, args));
    log.error(message, t);
  }

  public void error(String message, Throwable t) {
    action.addActionError(action.getText(message));
    log.error(message, t);
  }

  public void info(String message) {
    action.addActionMessage(action.getText(message));
    log.info(message);
  }

  public void info(String message, String[] args) {
    action.addActionMessage(action.getText(message, args));
    log.info(message);
  }

  public void info(String message, String[] args, Throwable t) {
    action.addActionMessage(action.getText(message, args));
    log.info(message, t);
  }

  public void info(String message, Throwable t) {
    action.addActionMessage(action.getText(message));
    log.info(message, t);
  }

  public void warn(String message) {
    info(message);
  }

  public void warn(String message, String[] args) {
    info(message, args);
  }

  public void warn(String message, String[] args, Throwable t) {
    info(message, args, t);
  }

  public void warn(String message, Throwable t) {
    info(message, t);
  }

}
