package summer.core.context.resolver;

import summer.core.context.annotation.Component;
import summer.core.context.annotation.Qualifier;

public class ComponentAnnotationResolver implements AnnotationResolver {
  @Override
  public boolean isSupported(Class<?> clazz) {
    return clazz.isAnnotationPresent(Component.class);
  }

  @Override
  public String resolve(Class<?> clazz) {
    String value = clazz.getAnnotation(Component.class).value();
    String componentName = value.isEmpty() ? getDefaultName(clazz): value;

    String qualifier = getQualifierValueIfPresent(clazz);

    return qualifier == null ? componentName : qualifier;
  }

  private String getQualifierValueIfPresent(Class<?> clazz) {
    return clazz.isAnnotationPresent(Qualifier.class) ? clazz.getAnnotation(Qualifier.class).value() : null;
  }
}
