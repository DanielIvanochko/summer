package penis;

import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;
import summer.core.context.annotation.Value;

@Component
public class Petya {
  private final Vasya vasya;
  private final Hui hui;
  private final Integer str;
  @Value("${str.str}")
  private Integer yooo;

  private Integer yeeee;

  @Autowired
  public Petya(Vasya vasya, Hui hui, @Value("${str.str}") Integer str) {
    this.vasya = vasya;
    this.hui = hui;
    this.str = str;
  }

  @Autowired
  public void setYeeee(@Value("${str.str}") Integer value) {
    this.yeeee = value;
  }
}
