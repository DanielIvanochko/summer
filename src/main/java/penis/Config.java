package penis;

import summer.core.context.annotation.Bean;
import summer.core.context.annotation.Configuration;

@Configuration
public class Config {
  @Bean
  public Hui hui() {
    return new Hui();
  }
}
