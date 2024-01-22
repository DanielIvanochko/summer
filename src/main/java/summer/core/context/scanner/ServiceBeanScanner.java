package summer.core.context.scanner;

import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import summer.core.context.annotation.Service;

import java.lang.annotation.Annotation;
import java.util.Set;

@RequiredArgsConstructor
public class ServiceBeanScanner implements ClassPathScanner {
  private final Reflections reflections;
  @Override
  public Set<Class<?>> scan() {
    return reflections.getTypesAnnotatedWith(Service.class);
  }

  @Override
  public Class<? extends Annotation> getSupportedAnnotation() {
    return Service.class;
  }
}
