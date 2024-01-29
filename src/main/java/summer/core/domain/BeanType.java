package summer.core.domain;

import lombok.Data;
import lombok.Getter;
import summer.core.context.annotation.Bean;
import summer.core.context.annotation.Component;
import summer.core.context.annotation.Configuration;
import summer.core.context.annotation.Service;
import summer.core.utils.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum BeanType {
  CONFIGURATION(1, Configuration.class),

  CONFIGURATION_BEAN(2, Bean.class),

  SERVICE(3, Service.class),

  COMPONENT(3, Component.class),

  UNDEFINED(4);
  private int order;
  private List<Class<? extends Annotation>> annotationClasses;

  BeanType(int order, Class<? extends Annotation>... annotationClasses) {
    this.order = order;
    this.annotationClasses = Arrays.asList(annotationClasses);
  }

  public static BeanType getBeanTypeByClass(Class<?> clazz) {
    var annotationToBeanMap = getAnnotationToBeanTypeMap();
    return Arrays.stream(clazz.getAnnotations())
        .map(annotation -> annotationToBeanMap.get(annotation.annotationType()))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(BeanType.UNDEFINED);
  }

  public static BeanType getBeanTypeByMethod(Method method) {
    var annotationToBeanMap = getAnnotationToBeanTypeMap();
    return Arrays.stream(method.getAnnotations())
        .map(annotation -> annotationToBeanMap.get(annotation.annotationType()))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(BeanType.UNDEFINED);
  }

  private static Map<Class<? extends Annotation>, BeanType> getAnnotationToBeanTypeMap() {
    return Arrays.stream(values())
        .flatMap(beanType -> beanType.getAnnotationClasses()
            .stream()
            .map(annotation -> Pair.of(annotation, beanType)))
        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
  }
}
