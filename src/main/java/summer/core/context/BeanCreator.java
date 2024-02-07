package summer.core.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.extern.log4j.Log4j;

import summer.core.context.exception.NoSuchBeanException;
import summer.core.context.injection.ConstructorBasedInjection;
import summer.core.context.injection.FieldBasedInjection;
import summer.core.context.injection.SetterBasedInjection;
import summer.core.domain.BeanDeclaration;
import summer.core.utils.ReflectionsHelper;

@Log4j
public class BeanCreator {
  private final AnnotationBeanRegistry beanRegistry;
  private final ConstructorBasedInjection constructorBasedInjection;
  private final FieldBasedInjection fieldBasedInjection;
  private final SetterBasedInjection setterBasedInjection;

  public BeanCreator(AnnotationBeanRegistry registry, ClassPathScannerFactory classPathScannerFactory) {
    beanRegistry = registry;
    constructorBasedInjection = new ConstructorBasedInjection(registry);
    fieldBasedInjection = new FieldBasedInjection(registry, classPathScannerFactory);
    setterBasedInjection = new SetterBasedInjection(registry, classPathScannerFactory);
  }

  public Object create(Class<?> clazz, String beanName, BeanDeclaration beanDeclaration) {
    log.info("Creating the bean:" + beanName);

    Object bean = constructorBasedInjection.createBean(clazz, beanName, beanDeclaration);
    injectDependencies(bean);
    return bean;
  }

  private void injectDependencies(Object bean) {
    fieldBasedInjection.injectFields(bean);
    setterBasedInjection.injectDependenciesForSetters(bean);
  }

  public Object createConfigurationBean(String beanName, BeanDeclaration beanDeclaration) {
    Object configBean = Optional.ofNullable(beanRegistry.getSingletonObjects().get(beanDeclaration.getFactoryBeanName()))
          .orElseThrow(() -> new NoSuchBeanException("Cannot find configuration bean to instantiate bean with name : " + beanName));
    List<String> methodParameterNames = ReflectionsHelper.getParameterNames(beanDeclaration.getMethod());
    List<Object> methodBeans = new ArrayList<>();

    methodParameterNames.forEach(parameter -> addBeanToMethodBeans(parameter, methodBeans));
    Supplier<Object> supplier = ReflectionsHelper.invokeBeanMethod(beanDeclaration.getMethod(), configBean, methodBeans.toArray());
    Object bean = supplier.get();

    if (beanDeclaration.isPrototype()) {
      beanRegistry.addPrototypeBean(beanName, supplier);
    } else {
      beanRegistry.addSingletonBean(beanName, bean);
    }

    return bean;
  }

  private void addBeanToMethodBeans(String factoryMethodParameterName, List<Object> methodBeans) {
    Object bean = beanRegistry.getBeanByName(factoryMethodParameterName);

    if (bean != null) {
      methodBeans.add(bean);
    } else {
      BeanDeclaration beanDeclaration = Optional.ofNullable(beanRegistry.getBeanDeclarationsMap().get(factoryMethodParameterName))
            .orElseThrow(() -> new NoSuchBeanException("Cannot find bean as a factory method parameter, beanName: " +  factoryMethodParameterName));
      if (beanDeclaration.isConfigurationBean()) {
        methodBeans.add(createConfigurationBean(factoryMethodParameterName, beanDeclaration));
      } else {
        methodBeans.add(create(beanDeclaration.getBeanClass(), factoryMethodParameterName, beanDeclaration));
      }
    }
  }
}
