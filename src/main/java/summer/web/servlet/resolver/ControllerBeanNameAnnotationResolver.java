package summer.web.servlet.resolver;

import summer.core.context.resolver.AnnotationResolver;
import summer.web.servlet.annotation.Controller;

public class ControllerBeanNameAnnotationResolver implements AnnotationResolver {
  @Override
  public boolean isSupported(Class<?> clazz) {
    return clazz.isAnnotationPresent(Controller.class);
  }

  @Override
  public String resolve(Class<?> clazz) {
    return getDefaultName(clazz);
  }
}
