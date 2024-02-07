package summer.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import summer.core.context.AnnotationBeanRegistry;

public class ListParameterResolver {
  public static Object getListParameter(AnnotationBeanRegistry beanRegistry,
                                        Parameter parameter) {
    var createdAnnotations = beanRegistry.getClassPathScannerFactory().getCreatedAnnotations();
    ParameterizedType genericTypeOfField = (ParameterizedType) parameter.getParameterizedType();
    List<Class<?>> dependencyClasses = ReflectionsHelper.extractImplClasses(genericTypeOfField, beanRegistry.getReflections(), createdAnnotations);
    return resolveListDependency(dependencyClasses, beanRegistry);
  }

  private static Object resolveListDependency(List<Class<?>> dependencyClasses, AnnotationBeanRegistry beanRegistry) {
    List<Object> dependencies = new ArrayList<>();
    for (Class<?> impl: dependencyClasses) {
      String beanName = beanRegistry.getClassPathScannerFactory().resolveBeanName(impl);
      Object dependency = beanRegistry.getOrCreateBean(beanName);
      dependencies.add(dependency);
    }
    return dependencies;
  }
}
