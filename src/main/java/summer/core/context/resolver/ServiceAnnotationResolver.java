package summer.core.context.resolver;

import summer.core.context.annotation.Service;

public class ServiceAnnotationResolver implements AnnotationResolver {
  @Override
  public boolean isSupported(Class<?> clazz) {
    return clazz.isAnnotationPresent(Service.class);
  }

  @Override
  public String resolve(Class<?> clazz) {
    String value = clazz.getAnnotation(Service.class).value();
    return value.isEmpty() ? getDefaultName(clazz) : value;
  }

}
