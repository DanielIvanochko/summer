package summer.web.server;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import summer.core.context.exception.SummerException;
import summer.web.server.exception.WebServerException;

@Slf4j
public class TomcatWebServer implements WebServer {
  @Getter
  private final Tomcat tomcat;

  public TomcatWebServer(Tomcat tomcat) {
    this.tomcat = tomcat;
  }

  @Override
  public void start() {
    try {
      tomcat.start();
      startDaemonThread();
      checkThatConnectorsHaveStarted();
      log.info("Tomcat has started on port(s): " + getPortsDescription(true));
    } catch (LifecycleException e) {
      stopSilently();
      destroySilently();
      throw new WebServerException("Can't start tomcat web server: " + e.getMessage());
    }
  }

  private void checkThatConnectorsHaveStarted() {
    checkConnectorHasStarted(this.tomcat.getConnector());
    for (Connector connector : this.tomcat.getService().findConnectors()) {
      checkConnectorHasStarted(connector);
    }
  }

  private void checkConnectorHasStarted(Connector connector) {
    if (LifecycleState.FAILED.equals(connector.getState())) {
      throw new SummerException("Cannot start tomcat");
    }
  }

  private List<Integer> getPortsDescription(boolean isLocalPort) {
    return Arrays.stream(tomcat.getService().findConnectors())
          .map(connector -> isLocalPort ? connector.getLocalPort(): connector.getPort())
          .collect(Collectors.toList());
  }

  private void stopSilently() {
    try {
      tomcat.stop();
    } catch (Exception e) {
      // ignoring exceptions
    }
  }

  private void destroySilently() {
    try {
      tomcat.destroy();
    } catch (Exception e) {
      // ignoring exceptions
    }
  }

  private void startDaemonThread() {
    Thread awaitThread = new Thread(() -> TomcatWebServer.this.tomcat.getServer().await());
    awaitThread.setContextClassLoader(getClass().getClassLoader());
    awaitThread.setDaemon(false);
    awaitThread.start();
  }

  @Override
  public void stop() {
    try {
      this.tomcat.stop();
    } catch (LifecycleException e) {
      throw new WebServerException("Can't stop Tomcat: " + e.getMessage());
    }
  }

  @Override
  public int getPort() {
    return Optional.ofNullable(tomcat.getConnector())
          .map(Connector::getPort)
          .orElse(-1);
  }
}
