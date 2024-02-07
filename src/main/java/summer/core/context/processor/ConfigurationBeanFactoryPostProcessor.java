package summer.core.context.processor;

import summer.core.context.annotation.Primary;
import summer.core.context.factory.DefaultBeanFactory;
import summer.core.domain.BeanDeclaration;
import summer.core.domain.BeanType;
import summer.core.utils.BeanUtils;
import summer.core.context.annotation.Bean;
import summer.core.context.exception.NoUniqueBeanException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

  @Override
  public void postProcessBeanFactory(DefaultBeanFactory defaultBeanFactory) {
    defaultBeanFactory.getAllBeanDeclarationNames()
        .forEach(name -> postProcessConfigurationBean(defaultBeanFactory, name));
  }

  private void postProcessConfigurationBean(DefaultBeanFactory defaultBeanFactory, String beanName) {
    BeanDeclaration beanDeclaration = defaultBeanFactory.getBeanDeclarationByName(beanName);

    if (beanDeclaration.isConfiguration()) {
      List<Method> annotatedMethods = getAnnotatedMethods(beanDeclaration.getBeanClass().getDeclaredMethods());
      for (Method method : annotatedMethods) {
        addBeanDeclarationForBean(method, defaultBeanFactory, beanName);
      }
    }
  }

  private void addBeanDeclarationForBean(Method method, DefaultBeanFactory defaultBeanFactory, String configBeanName) {
    BeanDeclaration beanDeclaration = BeanDeclaration.builder()
        .beanClass(method.getReturnType())
        .scope(BeanUtils.getBeanScope(method))
        .method(method)
        .type(BeanType.getBeanTypeByMethod(method))
        .factoryBeanName(configBeanName)
        .isPrimary(method.isAnnotationPresent(Primary.class))
        .build();

    String beanName = getBeanName(method);
    if (defaultBeanFactory.getBeanDeclarationsMap().containsKey(beanName)) {
      throw new NoUniqueBeanException(beanName);
    }

    defaultBeanFactory.addBeanDeclaration(beanName, beanDeclaration);
  }

  private String getBeanName(Method method) {
    Bean bean = method.getAnnotation(Bean.class);
    return bean.value().isEmpty() ? method.getName() : bean.value();
  }

  private List<Method> getAnnotatedMethods(Method[] methods) {
    return Arrays.stream(methods)
        .filter(method -> method.isAnnotationPresent(Bean.class))
        .collect(Collectors.toList());
  }
}
