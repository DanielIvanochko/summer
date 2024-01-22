package summer.core.context;

import org.reflections.Reflections;
import summer.core.context.annotation.Primary;
import summer.core.domain.BeanDeclaration;
import summer.core.domain.BeanType;
import summer.core.utils.BeanUtils;

import java.util.Comparator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SummerApplicationContext extends AnnotationBeanRegistry {
  public SummerApplicationContext(String... basePackage) {
    //scan the packages in search of annotations
    super(new Reflections(basePackage));
    Set<Class<?>> beanClasses = classPathScannerFactory.getBeansToCreate();

    register(beanClasses);

    //instantiate beans
    instantiateBeans();

    //post process beans
  }

  private void register(Set<Class<?>> classes) {
    classes.forEach(this::register);
  }

  private void instantiateBeans() {
    getBeanDeclarationsMap()
          .entrySet()
          .stream()
          .peek(entry -> log.info("Creating bean with name: " + entry.getKey()))
          .sorted(Comparator.comparing(entry -> entry.getValue().getType().getOrder()))
          .forEach(entry -> registerBean(entry.getKey(), entry.getValue()));
  }

  private void register(Class<?> clazz) {
    BeanDeclaration beanDeclaration = BeanDeclaration.builder()
        .beanClass(clazz)
        .factoryBeanName(clazz.getSimpleName())
        .isPrimary(clazz.isAnnotationPresent(Primary.class))
        .scope(BeanUtils.getBeanScope(clazz))
        .type(BeanType.getBeanTypeByClass(clazz))
//        .isProxy(false)
        .build();
    registerBeanDeclaration(beanDeclaration);
  }
}
