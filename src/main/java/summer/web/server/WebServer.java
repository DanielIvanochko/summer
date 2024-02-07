package summer.web.server;

public interface WebServer {
  void start();
  void stop();
  int getPort();
}
