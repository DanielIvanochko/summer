package summer.core.context;

import lombok.extern.log4j.Log4j;
import summer.core.context.exception.FieldInjectionException;
import summer.core.context.injection.ConstructorBasedInjection;
import summer.core.context.injection.FieldBasedInjection;
import summer.core.context.injection.SetterBasedInjection;
import summer.core.domain.BeanDeclaration;

@Log4j
public class BeanCreator {
  private final ConstructorBasedInjection constructorBasedInjection;
  private final FieldBasedInjection fieldBasedInjection;
  private final SetterBasedInjection setterBasedInjection;

  public BeanCreator(AnnotationBeanRegistry registry, ClassPathScannerFactory classPathScannerFactory) {
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
}
