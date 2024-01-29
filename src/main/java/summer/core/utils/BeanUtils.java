package summer.core.utils;

import summer.core.context.annotation.Scope;
import summer.core.domain.BeanScope;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanUtils {
  public static BeanScope getBeanScope(Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(Scope.class))
        .map(Scope::name)
        .filter(name -> getAllScopes().contains(name))
        .orElse(BeanScope.SINGLETON);
  }

  public static BeanScope getBeanScope(Method method) {
    return Optional.ofNullable(method.getAnnotation(Scope.class))
        .map(Scope::name)
        .filter(name -> getAllScopes().contains(name))
        .orElse(BeanScope.SINGLETON);
  }

  private static List<String> getAllScopes() {
    return Arrays.stream(BeanScope.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }
}
