package penis;

import java.util.List;

import summer.core.context.annotation.Autowired;
import summer.web.servlet.SummerServlet;
import summer.web.servlet.annotation.RestController;

@RestController
public class TestController implements SummerServlet {
  private final List<SomeInterface> someInterfaces;

  @Autowired
  public TestController(List<SomeInterface> someInterfaces) {
    this.someInterfaces = someInterfaces;
  }
}
