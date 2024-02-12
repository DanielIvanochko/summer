package summer.core.context.processor;


public interface BeanPostProcessor {
  default Object postProcessBean(String beanName, Object bean) {
    return bean;
  }
}
