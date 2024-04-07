import org.junit.jupiter.api.Test;
import predestroy.Printer;
import summer.web.SummerWebApplication;
import summer.web.servlet.WebStarter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreDestroyTest {

  @Test
  public void shouldRunPreDestroyMethod() {
    var context = SummerWebApplication.run("predestroy");

    WebStarter webStarter = context.getBean(WebStarter.class);
    Printer printer = context.getBean(Printer.class);

    webStarter.stop();

    String message = printer.getMessage();

    assertEquals("hello!", message);
  }
}
