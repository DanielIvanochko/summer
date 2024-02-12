package summer.web.servlet;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import jakarta.servlet.ServletContext;
import summer.core.context.SummerApplicationContext;
import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;
import summer.web.server.TomcatWebServer;
import summer.web.server.factory.TomcatWebServerFactory;

@Component
@Slf4j
public class WebStarter {
  private static final String REST_CONTROLLER_PARAMS = "REST_CONTROLLER_PARAMS";
  private static final String SUMMER_CONTEXT = "SUMMER_CONTEXT";
  private static final String DISPATCHER_SERVLET_NAME = "DISPATCHER_SERVLET";
  private final DispatcherServlet dispatcherServlet;
  private final TomcatWebServerFactory tomcatWebServerFactory;
  private final RestControllerContext restControllerContext;
  private final JsonExceptionHandler jsonExceptionHandler;
  private TomcatWebServer webServer;

  @Autowired
  public WebStarter(DispatcherServlet dispatcherServlet, TomcatWebServerFactory tomcatWebServerFactory, RestControllerContext restControllerContext, JsonExceptionHandler jsonExceptionHandler) {
    this.dispatcherServlet = dispatcherServlet;
    this.tomcatWebServerFactory = tomcatWebServerFactory;
    this.restControllerContext = restControllerContext;
    this.jsonExceptionHandler = jsonExceptionHandler;
  }

  public void run(SummerApplicationContext summerApplicationContext) {
    log.info("Starting Application...");
    webServer = (TomcatWebServer) tomcatWebServerFactory.getWebServer();
    webServer.start();
    var restControllerParams = restControllerContext.getParamsMap();
    Context context = tomcatWebServerFactory.getContext();
    ServletContext servletContext = context.getServletContext();

    context.addLifecycleListener(new SummerApplicationLifecycleListener(summerApplicationContext));
    context.getPipeline().addValve(jsonExceptionHandler);

    Tomcat tomcat = webServer.getTomcat();
    servletContext.setAttribute(SUMMER_CONTEXT, summerApplicationContext);
    servletContext.setAttribute(REST_CONTROLLER_PARAMS, restControllerParams);
    log.info("Registering Dispatcher Servlet");
    tomcat.addServlet(tomcatWebServerFactory.getContextPath(), DISPATCHER_SERVLET_NAME, dispatcherServlet);
    context.addServletMappingDecoded("/", DISPATCHER_SERVLET_NAME);
    log.info("Summer Application started");
  }

  public void stop() {
    webServer.stop();
  }
}
