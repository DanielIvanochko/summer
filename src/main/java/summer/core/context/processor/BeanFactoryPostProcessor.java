package summer.core.context.processor;

import summer.core.context.factory.DefaultBeanFactory;

public interface BeanFactoryPostProcessor {
  void postProcessBeanFactory(DefaultBeanFactory defaultBeanFactory);
}
