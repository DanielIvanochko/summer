package summer.core.context.injection;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import summer.core.context.annotation.Autowired;
import summer.core.context.exception.NoAutowiredConstructorException;
import summer.core.domain.BeanDeclaration;

public class ConstructorBasedInjection {
  public Object createBean(Class<?> clazz, String beanName, BeanDeclaration beanDeclaration) {
    Constructor<?> autowiredConstructor = findAutowiredConstructor(clazz);
    return instantiateBean(clazz, beanName, beanDeclaration);
  }

  private Object instantiateBean(Class<?> clazz, String beanName, BeanDeclaration beanDeclaration) {
    return null;
  }

  private Constructor<?> findAutowiredConstructor(Class<?> clazz) {
    Constructor<?>[] constructors = clazz.getConstructors();

    if (constructors.length == 1) {
      return constructors[0];
    }

    return Arrays.stream(constructors)
          .filter(constructor -> constructor.isAnnotationPresent(Autowired.class))
          .findFirst()
          .orElseThrow(() -> new NoAutowiredConstructorException("No constructor for class:" + clazz.getSimpleName()));
  }
}
