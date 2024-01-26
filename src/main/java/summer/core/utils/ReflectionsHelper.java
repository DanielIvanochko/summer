package summer.core.utils;

import summer.core.context.annotation.PostConstruct;
import summer.core.context.exception.SummerException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

  public static List<String> getParameterNamesOfConstructor(Constructor<?> constructor) {
    return Arrays.stream(constructor.getParameterTypes())
        .map(Class::getSimpleName)
        .map(ReflectionsHelper::getSimpleClassName)
        .collect(Collectors.toList());
  }

  public static Supplier<Object> createObjectWithParameters(Constructor<?> constructor, Object[] dependencies) {

    return () -> {
      try {
        return constructor.newInstance(dependencies);
      } catch (Exception e) {
        throw new SummerException("Cannot create the object: " + e.getMessage());
      }
    };
  }

  public static void setField(Object bean, Field field, Object value) throws IllegalAccessException {
    field.setAccessible(true);
    field.set(bean, value);
  }

  private static String getSimpleClassName(String name) {
    char[] array = name.toCharArray();
    array[0] = Character.toLowerCase(array[0]);
    return new String(array);
  }

  public static void processBeanPostProcessorAnnotation(Object bean, Method[] methods, Class<? extends Annotation> annotation) throws Exception {
    for (Method method: methods) {
      if (method.isAnnotationPresent(annotation)) {
        method.invoke(bean);
      }
    }
  }
}
