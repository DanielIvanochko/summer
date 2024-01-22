package summer.core;

import summer.core.context.SummerApplicationContext;

public class SummerApplication {
  private static String MAIN_PACKAGE = "summer.core";

  public static SummerApplicationContext run(String basePackage) {
    String[] packages = new String[]{MAIN_PACKAGE, basePackage};
    SummerApplicationContext summerApplicationContext = new SummerApplicationContext(packages);
    return summerApplicationContext;
  }
}
