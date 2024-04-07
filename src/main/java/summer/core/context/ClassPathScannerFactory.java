package summer.core.context;

import org.reflections.Reflections;

import summer.core.context.resolver.AnnotationResolver;
import summer.core.context.scanner.ClassPathScanner;
import summer.core.utils.ReflectionsHelper;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

public class ClassPathScannerFactory {
  private final List<ClassPathScanner> classPathScanners;
  private final List<AnnotationResolver> annotationResolvers;
  @Getter
  private final List<Class<? extends Annotation>> createdAnnotations;

  public ClassPathScannerFactory(Reflections reflections) {
    this.classPathScanners = reflections.getSubTypesOf(ClassPathScanner.class)
        .stream()
        .map(clazz -> clazz.cast(ReflectionsHelper.createObjectWithOneParameter(clazz, Reflections.class, reflections)))
        .collect(Collectors.toList());

    this.createdAnnotations = classPathScanners.stream()
        .map(ClassPathScanner::getSupportedAnnotation)
        .collect(Collectors.toList());

    this.annotationResolvers = reflections.getSubTypesOf(AnnotationResolver.class)
        .stream()
        .map(clazz -> clazz.cast(ReflectionsHelper.createObjectWithoutParameters(clazz)))
        .collect(Collectors.toList());
  }

  public Set<Class<?>> getBeansToCreate() {
    return classPathScanners.stream()
        .flatMap(classPathScanner -> classPathScanner.scan().stream())
        .collect(Collectors.toSet());
  }

  public String resolveBeanName(Class<?> clazz) {
    return annotationResolvers.stream()
        .filter(resolver -> resolver.isSupported(clazz))
        .findFirst()
        .map(resolver -> resolver.resolve(clazz))
        .orElse(null);
  }
}
