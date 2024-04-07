package summer.core.utils;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import summer.core.context.exception.SummerException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

import org.reflections.Reflections;

public class ReflectionsHelper {
  private static final Paranamer paranamer = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));
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

  public static List<String> getParameterClassNames(Method method) {
    return Arrays.stream(method.getParameters())
          .map(parameter -> parameter.getClass().getSimpleName())
          .map(ReflectionsHelper::getSimpleClassName)
          .collect(Collectors.toList());
  }

  public static List<String> getParameterNames(Method method) {
    return Arrays.asList(paranamer.lookupParameterNames(method));
  }

  public static Supplier<Object> invokeBeanMethod(Method method, Object configBean, Object[] parameters) {
    return () -> {
      try {
        return method.invoke(configBean, parameters);
      } catch (Exception e) {
        throw new SummerException(e.getMessage());
      }
    };
  }

  @SneakyThrows
  public static List<Class<?>> extractImplClasses(ParameterizedType genericTypeOfField, Reflections reflections, List<Class<? extends Annotation>> createdAnnotations) {
    Type actualType = genericTypeOfField.getActualTypeArguments()[0];
    if (actualType instanceof Class actualTypeArgument) {
      String name = actualTypeArgument.getName();
      Class<?> interfaceClass = Class.forName(name);
      return reflections.getSubTypesOf(interfaceClass)
            .stream()
            .filter(impl -> isAnnotationPresentOnImpl(impl, createdAnnotations))
            .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private static boolean isAnnotationPresentOnImpl(Class<?> impl, List<Class<? extends Annotation>> createdAnnotations) {
    return Arrays.stream(impl.getAnnotations())
          .map(Annotation::annotationType)
          .anyMatch(createdAnnotations::contains);
  }
}
