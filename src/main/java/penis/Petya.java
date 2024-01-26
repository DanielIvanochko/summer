package penis;

import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;

@Component
public class Petya {
  @Autowired
  private Vasya vasya;
  private Hui hui;

  @Autowired
  public void setHui(Hui hui) {
    this.hui = hui;
  }
}
