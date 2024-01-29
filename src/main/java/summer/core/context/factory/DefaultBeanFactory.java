package summer.core.context.factory;

import summer.core.context.annotation.PreDestroy;
import summer.core.context.exception.NoSuchBeanException;
import summer.core.context.exception.PreDestroyException;
import summer.core.domain.BeanDeclaration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import summer.core.utils.ReflectionsHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Getter
public class DefaultBeanFactory implements BeanFactory {

  private final Map<String, BeanDeclaration> beanDeclarationsMap = new ConcurrentHashMap<>();

  private final Map<Class<?>, List<String>> typeToBeanNames = new ConcurrentHashMap<>();

  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

  private final Map<String, Supplier<Object>> prototypeSuppliers = new ConcurrentHashMap<>();

  @Setter
  private Map<String, String> properties = new ConcurrentHashMap<>();

  @Setter
  private String profileName;


  public <T> T getBean(Class<T> type) {
    Map<String, T> beans = getBeans(type);

    if (beans.size() > 1) {
      return getPrimary(beans, type);
    }

    return beans.values().stream()
          .findFirst()
          .orElseThrow(() -> {
            if (type.isInterface()) {
              return new NoSuchBeanException(String.format("No such bean that implements this %s ", type));
            }
            return new NoSuchBeanException(type.getSimpleName());
          });
  }

  public <T> T getBean(Class<T> type, String name) {
    Object bean = Optional.ofNullable(getBeanByName(name))
          .orElseThrow(() -> {
            if (type.isInterface()) {
              return new NoSuchBeanException(String.format("No such bean that implements this %s ", type));
            }
            return new NoSuchBeanException(type.getSimpleName());
          });

    return type.cast(bean);
  }


  public <T> Map<String, T> getBeans(Class<T> type) {
    List<String> beanNames = typeToBeanNames.entrySet().stream()
          .filter(entry -> type.isAssignableFrom(entry.getKey()))
          .map(Entry::getValue)
          .flatMap(Collection::stream)
          .toList();

    return beanNames.stream()
          .collect(Collectors.toMap(Function.identity(), name -> getBean(type, name)));
  }

  public Object getBeanByName(String beanName) {
    return Optional.ofNullable(getPrototypeSuppliers().get(beanName))
          .map(Supplier::get)
          .orElse(getSingletonObjects().get(beanName));
  }

  public <T> Map<String, T> getAllBeans() {
    return (Map<String, T>) singletonObjects;
  }


  public void close() {
    for (var bean : singletonObjects.values()) {
      var declaredMethods = bean.getClass().getMethods();
      try {
        ReflectionsHelper.processBeanPostProcessorAnnotation(bean, declaredMethods, PreDestroy.class);
      } catch (Exception e) {
        throw new PreDestroyException(e.getMessage());
      }
    }
  }

  public void addSingletonBean(String beanName, Object bean) {
    singletonObjects.put(beanName, bean);
  }

  public void addPrototypeBean(String beanName, Supplier<Object> supplier) {
    prototypeSuppliers.put(beanName, supplier);
  }

  public void addBeanDeclaration(String beanName, BeanDeclaration beanDefinition) {
    log.debug("Registering BeanDefinition of [{}]", beanName);
    this.beanDeclarationsMap.put(beanName, beanDefinition);

    List<String> beanNames = typeToBeanNames.getOrDefault(beanDefinition.getBeanClass(), new ArrayList<>());
    beanNames.add(beanName);
    typeToBeanNames.put(beanDefinition.getBeanClass(), beanNames);
  }

  /**
   * Retrieves the bean definition associated with the given bean name.
   *
   * @param beanName The name of the bean for which the definition is requested.
   * @return The BeanDefinition object associated with the provided bean name.
   */
  public BeanDeclaration getBeanDeclarationByName(String beanName) {
    return this.beanDeclarationsMap.get(beanName);
  }

  /**
   * Retrieves a list containing names of all registered bean definitions in the factory.
   *
   * @return A list containing the names of all registered bean definitions.
   */
  public List<String> getAllBeanDeclarationNames() {
    return this.beanDeclarationsMap.keySet().stream().toList();
  }

  /**
   * Checks if a bean with the specified name has been created.
   *
   * @param beanName The name of the bean to check.
   * @return true if the bean has been created, false otherwise.
   */
  public boolean isBeanCreated(String beanName) {
    return singletonObjects.containsKey(beanName) || prototypeSuppliers.containsKey(beanName);
  }

  private <T> T getPrimary (Map<String, T> beans, Class<T> type) {
    List <T> foundBeans = beans.entrySet()
          .stream()
          .filter(entry -> getBeanDeclarationByName(entry.getKey()).isPrimary())
          .map(Entry::getValue)
          .toList();
    if (foundBeans.size() != 1) {
      throw new RuntimeException("type gimno");
//      throw new NoUniqueBeanException(type);
    } else {
      return foundBeans.get(0);
    }
  }
}
