package summer.web.server.config;

import lombok.Data;

import summer.core.context.annotation.Component;
import summer.core.context.annotation.Value;

@Data
@Component
public class ServerProperties {
  @Value("${server.port:8080}")
  private int port;
  @Value("${server.contextPath:}")
  private String contextPath;

  @Value("${server.withStackTrace:false}")
  private boolean withStackTrace;
}
