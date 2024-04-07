package summer.core.context.factory;

import summer.core.domain.BeanDeclaration;
import summer.core.utils.ReflectionsHelper;
import summer.core.context.annotation.PreDestroy;
import summer.core.context.exception.NoSuchBeanException;
import summer.core.context.exception.NoUniqueBeanException;
import summer.core.context.exception.PreDestroyException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;
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
  @Getter
  private Properties properties;

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

  public BeanDeclaration getBeanDeclarationByName(String beanName) {
    return this.beanDeclarationsMap.get(beanName);
  }

  public List<String> getAllBeanDeclarationNames() {
    return this.beanDeclarationsMap.keySet().stream().toList();
  }

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
      throw new NoUniqueBeanException(type.getSimpleName());
    } else {
      return foundBeans.get(0);
    }
  }
}
