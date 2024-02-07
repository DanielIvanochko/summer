package summer.web.server.factory;

import org.apache.catalina.Context;

import summer.web.server.WebServer;

public interface WebServerFactory {
  WebServer getWebServer();
  Context getContext();
  String getContextPath();
}
