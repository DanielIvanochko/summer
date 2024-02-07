package summer.web.servlet;

import summer.core.context.SummerApplicationContext;
import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;
import summer.web.server.TomcatWebServer;
import summer.web.server.WebServer;
import summer.web.server.factory.TomcatWebServerFactory;

@Component
public class WebStarter {
  private final TomcatWebServerFactory tomcatWebServerFactory;

  @Autowired
  public WebStarter(TomcatWebServerFactory tomcatWebServerFactory) {
    this.tomcatWebServerFactory = tomcatWebServerFactory;
  }

  public void run(SummerApplicationContext context) {
    WebServer webServer = tomcatWebServerFactory.getWebServer();
    webServer.start();
  }
}
