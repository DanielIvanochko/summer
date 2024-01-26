package summer.core.context.injection;

import lombok.RequiredArgsConstructor;
import summer.core.context.AnnotationBeanRegistry;
import summer.core.context.ClassPathScannerFactory;
import summer.core.context.annotation.Autowired;
import summer.core.context.exception.NoSuchBeanException;
import summer.core.context.exception.SummerException;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class SetterBasedInjection {
  private final AnnotationBeanRegistry beanRegistry;
  private final ClassPathScannerFactory classPathScannerFactory;

  public void injectDependenciesForSetters(Object bean) {

    Method[] methods = bean.getClass().getDeclaredMethods();

    for (Method method : methods) {
      if (method.isAnnotationPresent(Autowired.class)) {
        Class<?> parameterType = method.getParameterTypes()[0];

        String dependencyName = classPathScannerFactory.resolveBeanName(parameterType);

        checkMethodDependency(dependencyName, parameterType);

        Object dependency = beanRegistry.getOrCreateBean(dependencyName);

        injectDependencyInMethod(method, bean, dependency);
      }
    }
  }

  private void injectDependencyInMethod(Method method, Object bean, Object dependency) {
    try {
      method.invoke(bean, dependency);
    } catch (Exception e) {
      throw new SummerException(e.getMessage());
    }
  }

  private void checkMethodDependency(String dependencyName, Class<?> parameterType) {
    if (dependencyName == null) {
      if (parameterType.isInterface()) {
        throw new NoSuchBeanException("There is no bean that implements " + parameterType.getSimpleName());
      }
      throw new NoSuchBeanException("Cannot find the bean with type : " + parameterType.getSimpleName());
    }
  }
}
