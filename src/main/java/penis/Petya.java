package penis;

import summer.core.context.annotation.Autowired;
import summer.core.context.annotation.Component;
import summer.core.context.annotation.Value;

@Component
public class Petya implements SomeInterface {
  private final Vasya vasya;
  private final Hui hui;
  private final Integer str;
  @Value("${str.str:123}")
  private Integer yooo;

  private Integer yeeee;

  @Autowired
  public Petya(Vasya vasya, Hui hui, @Value("${str.str:343}") Integer str) {
    this.vasya = vasya;
    this.hui = hui;
    this.str = str;
  }

  @Autowired
  public void setYeeee(@Value("${str.str:7676}") Integer value) {
    this.yeeee = value;
  }
}
