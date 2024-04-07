package predestroy;

import lombok.Getter;
import summer.core.context.annotation.Component;
import summer.core.context.annotation.PreDestroy;

@Component
@Getter
public class Printer {
  private String message;

  @PreDestroy
  public void preDestroy() {
    message = "hello!";
  }

}
