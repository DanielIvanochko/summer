package penis;


import summer.web.servlet.SummerServlet;
import summer.web.servlet.annotation.GetMapping;
import summer.web.servlet.annotation.RestController;

@RestController
public class TestController implements SummerServlet {

  @GetMapping(path = "/test")
  public String get() {
    return "hui";
  }
}
