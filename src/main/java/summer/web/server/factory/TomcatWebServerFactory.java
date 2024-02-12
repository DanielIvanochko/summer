package summer.web.server.factory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import summer.web.server.TomcatWebServer;
import summer.web.server.WebServer;
import summer.web.server.exception.WebServerException;

public class TomcatWebServerFactory implements WebServerFactory {
  @Getter
  @Setter
  private int port;

  @Getter
  @Setter
  private String contextPath;

  @Getter
  private Context context;

  @Setter
  private File baseDirectory;

  public TomcatWebServerFactory(int port, String contextPath) {
    this.port = port;
    this.contextPath = contextPath;
  }

  @Override
  public WebServer getWebServer() {
    Tomcat tomcat = new Tomcat();

    File baseDir = Optional.ofNullable(baseDirectory)
          .orElseGet(this::createTemporaryDirectory);

    String directoryPath = baseDir.getAbsolutePath();
    tomcat.setBaseDir(directoryPath);
    tomcat.setPort(port);
    context = tomcat.addWebapp(contextPath, directoryPath);
    return new TomcatWebServer(tomcat);
  }

  private File createTemporaryDirectory() {
    try {
      File tempDir = Files.createTempDirectory("tomcat." + getPort()).toFile();
      tempDir.deleteOnExit();
      return tempDir;
    } catch (IOException e) {
      throw new WebServerException("Unable to create temporary directory for tomcat :" + e.getMessage());
    }
  }
}
