package summer.core.utils;

import java.lang.reflect.InvocationTargetException;

public class ReflectionsHelper {
  public static Object createObjectWithOneParameter(Class<?> clazz, Class<?> parameterType, Object parameter) {
    try {
      return clazz.getConstructor(parameterType).newInstance(parameter);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Object createObjectWithoutParameters(Class<?> clazz) {
    try {
      return clazz.getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
