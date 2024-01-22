package summer.core.context.resolvers;

import summer.core.context.annotation.Component;
import summer.core.context.annotation.Configuration;
import summer.core.context.annotation.Qualifier;
import summer.core.context.annotation.Service;

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
