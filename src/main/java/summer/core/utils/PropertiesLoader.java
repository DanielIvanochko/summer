package summer.core.utils;

import summer.core.context.exception.SummerException;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
  public static Properties loadProperties(String fileName) {
    ClassLoader classLoader = PropertiesLoader.class.getClassLoader();
    Properties properties = new Properties();
    try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
      properties.load(inputStream);
      return properties;
    } catch (Exception e) {
      throw new SummerException("Couldn't load application.properties : " + e.getMessage());
    }
  }
}
