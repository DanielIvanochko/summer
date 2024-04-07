package summer.core.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import summer.core.context.exception.CyclingBeanException;
import summer.core.context.factory.DefaultBeanFactory;
import summer.core.domain.BeanDeclaration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Slf4j
public class AnnotationBeanRegistry extends DefaultBeanFactory implements BeanDeclarationRegistry {
  private Reflections reflections;
  protected ClassPathScannerFactory classPathScannerFactory;
  private final BeanCreator beanCreator;
  private final Set<String> currentlyCreatingBeans = new HashSet<>();

  public AnnotationBeanRegistry(Reflections reflections) {
    this.reflections = reflections;
    this.classPathScannerFactory = new ClassPathScannerFactory(reflections);
    beanCreator = new BeanCreator(this, classPathScannerFactory);
  }

  @Override
  public void registerBeanDeclaration(BeanDeclaration beanDeclaration) {
    String name = classPathScannerFactory.resolveBeanName(beanDeclaration.getBeanClass());
    addBeanDeclaration(name, beanDeclaration);
  }


  protected void registerBean(String beanName, BeanDeclaration beanDeclaration) {
    Class<?> clazz = beanDeclaration.getBeanClass();

    if (currentlyCreatingBeans.contains(beanName)) {
      throw new CyclingBeanException("Cyclic dependency found:" + currentlyCreatingBeans);
    }

    if (isBeanCreated(beanName)) {
      log.info("Bean is already created, no need to create it once more");
      return;
    }

    currentlyCreatingBeans.add(beanName);

    if (beanDeclaration.isConfigurationBean()) {
      beanCreator.createConfigurationBean(beanName, beanDeclaration);
    } else {
      beanCreator.create(clazz, beanName, beanDeclaration);
    }

    currentlyCreatingBeans.clear();
  }

  public Object getOrCreateBean(String dependencyBeanName) {
    Object existingBean = getBeanByName(dependencyBeanName);

    return Optional.ofNullable(existingBean)
        .orElseGet(() -> createBeanIfNeeded(dependencyBeanName));
  }

  private Object createBeanIfNeeded(String beanName) {
    BeanDeclaration beanDeclaration = getBeanDeclarationByName(beanName);

    Optional.ofNullable(beanDeclaration).ifPresent(beanDec -> registerBean(beanName, beanDeclaration));

    return getBeanByName(beanName);
  }
}
