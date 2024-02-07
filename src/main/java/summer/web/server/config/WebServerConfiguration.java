package summer.web.server.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Bean;
import summer.core.context.annotation.Configuration;
import summer.web.server.factory.TomcatWebServerFactory;

@Configuration
public class WebServerConfiguration {

  @Autowired
  private ServerProperties serverProperties;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }

  @Bean
  public TomcatWebServerFactory tomcatWebServerFactory() {
    return new TomcatWebServerFactory(serverProperties.getPort(), serverProperties.getContextPath());
  }
}
