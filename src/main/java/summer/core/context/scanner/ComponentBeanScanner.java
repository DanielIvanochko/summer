package summer.core.context.scanner;

import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import summer.core.context.annotation.Component;

import java.lang.annotation.Annotation;
import java.util.Set;

@RequiredArgsConstructor
public class ComponentBeanScanner implements ClassPathScanner{
  private final Reflections reflections;


  @Override
  public Set<Class<?>> scan() {
    return reflections.getTypesAnnotatedWith(Component.class);
  }

  @Override
  public Class<? extends Annotation> getSupportedAnnotation() {
    return Component.class;
  }
}
