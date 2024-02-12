package summer.web.servlet.resolver;

import summer.core.context.resolver.AnnotationResolver;
import summer.web.servlet.annotation.RestController;

public class RestControllerBeanNameAnnotationResolver implements AnnotationResolver {
  @Override
  public boolean isSupported(Class<?> clazz) {
    return clazz.isAnnotationPresent(RestController.class);
  }

  @Override
  public String resolve(Class<?> clazz) {
    return getDefaultName(clazz);
  }
}
