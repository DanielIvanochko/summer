package summer.core.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import summer.core.context.factory.DefaultBringBeanFactory;
import summer.core.domain.BeanDeclaration;

@Getter
@Slf4j
public class AnnotationBeanRegistry extends DefaultBringBeanFactory implements BeanDeclarationRegistry {
  private Reflections reflections;
  protected ClassPathScannerFactory classPathScannerFactory;
  private final BeanCreator beanCreator;

  public AnnotationBeanRegistry(Reflections reflections) {
    this.reflections = reflections;
    this.classPathScannerFactory = new ClassPathScannerFactory(reflections);
    beanCreator = new BeanCreator(this, reflections);
  }

  @Override
  public void registerBeanDeclaration(BeanDeclaration beanDeclaration) {
    String name = classPathScannerFactory.resolveBeanName(beanDeclaration.getBeanClass());
    addBeanDeclaration(name, beanDeclaration);
  }

  /*private void addBeanDeclaration(String name, BeanDeclaration beanDeclaration) {
    log.info("Adding Bean Declaration for : " + name);
    beanDeclarationsMap.put(name, beanDeclaration);
    typeToBeanNames.put(beanDeclaration.getBeanClass(), name);
  }*/

  protected void registerBean(String beanName, BeanDeclaration beanDeclaration) {
    //create bean
    //
  }
}
