package summer.web.servlet.scanner;

import java.lang.annotation.Annotation;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.reflections.Reflections;

import summer.core.context.scanner.ClassPathScanner;
import summer.web.servlet.annotation.Controller;

@RequiredArgsConstructor
public class ControllerClassPathScanner implements ClassPathScanner {
  private final Reflections reflections;

  @Override
  public Set<Class<?>> scan() {
    return reflections.getTypesAnnotatedWith(getSupportedAnnotation());
  }

  @Override
  public Class<? extends Annotation> getSupportedAnnotation() {
    return Controller.class;
  }
}
