package summer.web;

import summer.core.context.SummerApplicationContext;
import summer.web.servlet.WebStarter;

public class SummerWebApplication {
  private static final String MAIN_WEB_PACKAGE = "summer.web";
  private static final String MAIN_CORE_PACKAGE = "summer.core";

  public static SummerApplicationContext run(String basePackage) {
    String[] packages = new String[]{MAIN_CORE_PACKAGE, MAIN_WEB_PACKAGE, basePackage};
    SummerApplicationContext context = new SummerApplicationContext(packages);

    WebStarter webStarter = context.getBean(WebStarter.class);
    webStarter.run(context);

    return context;
  }
}
