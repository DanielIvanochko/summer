package summer.core.context.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import summer.core.context.AnnotationBeanRegistry;
import summer.core.context.annotation.Autowired;
import summer.core.context.exception.NoAutowiredConstructorException;
import summer.core.context.exception.NoSuchBeanException;
import summer.core.context.exception.NoUniqueBeanException;
import summer.core.domain.BeanDeclaration;
import summer.core.utils.ReflectionsHelper;

@RequiredArgsConstructor
public class ConstructorBasedInjection {
  private final AnnotationBeanRegistry beanRegistry;

  public Object createBean(Class<?> clazz, String beanName, BeanDeclaration beanDeclaration) {
    Constructor<?> constructor = findAutowiredConstructor(clazz);

    return createBeanUsingConstructor(constructor, beanName, beanDeclaration);
  }

  private Object createBeanUsingConstructor(Constructor<?> constructor, String beanName, BeanDeclaration beanDeclaration) {
    Parameter[] parameters = constructor.getParameters();
    Object[] dependencies = new Object[parameters.length];
    List<String> parameterNames = ReflectionsHelper.getParameterNamesOfConstructor(constructor);

    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      String parameterName = parameterNames.get(i);

      String dependencyBeanName = findBeanNameForArgumentInConstructor(parameter, parameterName);
      Object dependencyObject = beanRegistry.getOrCreateBean(dependencyBeanName);
      dependencies[i] = dependencyObject;
    }

    Supplier<Object> beanSupplier = ReflectionsHelper.createObjectWithParameters(constructor, dependencies);
    Object bean = beanSupplier.get();

    if (beanDeclaration.isPrototype()) {
      beanRegistry.addPrototypeBean(beanName, beanSupplier);
    } else {
      beanRegistry.addSingletonBean(beanName, bean);
    }

    return bean;
  }

  private String findBeanNameForArgumentInConstructor(Parameter parameter, String parameterName) {
    Class<?> clazz = parameter.getType();

    List<String> beanNames;

    if (clazz.isInterface()) {
      beanNames = beanRegistry.getTypeToBeanNames().entrySet().stream()
          .filter(entry -> clazz.isAssignableFrom(entry.getKey()))
          .map(Map.Entry::getValue)
          .flatMap(Collection::stream)
          .toList();
    } else {
      beanNames = Optional.ofNullable(beanRegistry.getTypeToBeanNames().get(clazz)).orElse(new ArrayList<>());
    }

    checkIfTheDependencyFound(beanNames, clazz);
    if (beanNames.size() == 1) {
      return beanNames.get(0);
    } else {
      return findBeanNameBasedOnPriority(beanNames, parameterName, parameter);
    }
  }

  private String findBeanNameBasedOnPriority(List<String> beanNames, String parameterName, Parameter parameter) {
    Class<?> parameterType = parameter.getType();

    List<String> primaryNames = beanNames.stream()
        .filter(name -> beanRegistry.getBeanDeclarationByName(name).isPrimary())
        .toList();

    if (primaryNames.size() == 1) {
      return primaryNames.get(0);
    } else if (primaryNames.size() > 1) {
      throw new NoUniqueBeanException("No unique bean for " + parameterType.getSimpleName());
    }
    return beanNames.stream()
        .filter(name -> name.equalsIgnoreCase(parameterName))
        .findFirst()
        .orElseThrow(() -> new NoSuchBeanException("No such bean for parameter" + parameterName));
  }

  private void checkIfTheDependencyFound(List<String> beanNames, Class<?> clazz) {
    if (beanNames.isEmpty()) {
      if (clazz.isInterface()) {
        throw new NoSuchBeanException("No such bean that implements " + clazz.getSimpleName());
      }
      throw new NoSuchBeanException("Cannot find dependency bean " + clazz.getSimpleName());
    }
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
