package summer.core.utils;

import summer.core.context.annotation.Scope;
import summer.core.domain.BeanScope;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BeanUtils {
  public static BeanScope getBeanScope(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Scope.class)) {
      BeanScope scope = clazz.getAnnotation(Scope.class).name();
      return getBeanScope(scope);
    }
    return BeanScope.SINGLETON;
  }

  public static BeanScope getBeanScope(Method method) {
    if (method.isAnnotationPresent(Scope.class)) {
      BeanScope scope = method.getAnnotation(Scope.class).name();
      return getBeanScope(scope);
    }
    return BeanScope.SINGLETON;
  }

  private static BeanScope getBeanScope(BeanScope scope) {
    return getAllScopes().stream()
        .filter(currentScope -> currentScope.equalsIgnoreCase(scope.name()))
        .findFirst()
        .map(BeanScope::valueOf)
        .orElse(BeanScope.SINGLETON);
  }

  private static List<String> getAllScopes() {
    return Arrays.stream(BeanScope.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }
}
