package penis;

import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;

@Component
public class Petya {
  private final Vasya vasya;
  private final Hui hui;

  @Autowired
  public Petya(Vasya vasya, Hui hui) {
    this.vasya = vasya;
    this.hui = hui;
  }
}
