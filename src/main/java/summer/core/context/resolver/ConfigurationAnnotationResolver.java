package summer.core.context.resolver;

import summer.core.context.annotation.Configuration;

public class ConfigurationAnnotationResolver implements AnnotationResolver {
  @Override
  public boolean isSupported(Class<?> clazz) {
    return clazz.isAnnotationPresent(Configuration.class);
  }

  @Override
  public String resolve(Class<?> clazz) {
    return getDefaultName(clazz);
  }

}
